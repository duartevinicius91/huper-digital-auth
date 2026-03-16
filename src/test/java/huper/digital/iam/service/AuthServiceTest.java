package huper.digital.iam.service;

import huper.digital.iam.dto.LoginRequest;
import huper.digital.iam.dto.RefreshTokenRequest;
import huper.digital.iam.dto.TokenResponse;
import huper.digital.iam.exception.AuthenticationException;
import huper.digital.iam.entity.AuthRefreshTokenEntity;
import huper.digital.iam.entity.AuthUserEntity;
import huper.digital.iam.repository.AuthRefreshTokenRepository;
import huper.digital.iam.repository.AuthUserRepository;
import huper.digital.iam.security.AuthAccessService;
import huper.digital.iam.security.JwtTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private AuthRefreshTokenRepository refreshTokenRepository;

    @Mock
    private AuthAccessService accessService;

    @Mock
    private JwtTokenService jwtTokenService;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private RefreshTokenRequest refreshTokenRequest;
    private TokenResponse tokenResponse;
    private AuthUserEntity user;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("test@example.com", "password");
        refreshTokenRequest = new RefreshTokenRequest("refresh-token");
        tokenResponse = new TokenResponse("access-token", "refresh-token", 3600L);

        user = new AuthUserEntity();
        user.setId("user-id");
        user.setEmail("test@example.com");
        user.setEnabled(true);
        user.setPasswordHash("$2a$10$hash");
    }

    @Test
    void testAuthenticate_ShouldReturnTokenResponse() {
        when(authUserRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtTokenService.matchesPassword("password", "$2a$10$hash")).thenReturn(true);
        when(accessService.resolveRoleNames(user)).thenReturn(Set.of("USER_MANAGEMENT_READ"));
        when(jwtTokenService.generateAccessToken(eq(user), anySet())).thenReturn("access-token");
        when(jwtTokenService.createAndPersistRefreshToken(eq(user), eq(refreshTokenRepository))).thenReturn("refresh-token");
        when(jwtTokenService.accessTokenTtlSeconds()).thenReturn(3600L);

        TokenResponse result = authService.authenticate(loginRequest);

        assertNotNull(result);
        assertEquals("access-token", result.accessToken());
        verify(authUserRepository).findByEmail("test@example.com");
    }

    @Test
    void testAuthenticate_WhenExceptionOccurs_ShouldThrowAuthenticationException() {
        when(authUserRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(AuthenticationException.class, () -> authService.authenticate(loginRequest));
        verify(authUserRepository).findByEmail(anyString());
    }

    @Test
    void testRefreshToken_ShouldReturnTokenResponse() {
        AuthRefreshTokenEntity refresh = new AuthRefreshTokenEntity();
        refresh.setId(1L);
        refresh.setUser(user);
        refresh.setToken("refresh-token");
        refresh.setExpiresAt(LocalDateTime.now().plusDays(1));
        refresh.setRevoked(false);

        when(refreshTokenRepository.findValidByToken(eq("refresh-token"), any(LocalDateTime.class)))
            .thenReturn(Optional.of(refresh));
        when(accessService.resolveRoleNames(user)).thenReturn(Set.of("USER_MANAGEMENT_READ"));
        when(jwtTokenService.generateAccessToken(eq(user), anySet())).thenReturn("access-token");
        when(jwtTokenService.createAndPersistRefreshToken(eq(user), eq(refreshTokenRepository))).thenReturn("new-refresh-token");
        when(jwtTokenService.accessTokenTtlSeconds()).thenReturn(3600L);

        TokenResponse result = authService.refreshToken(refreshTokenRequest);

        assertNotNull(result);
        assertEquals("access-token", result.accessToken());
        assertEquals("new-refresh-token", result.refreshToken());
        verify(refreshTokenRepository).findValidByToken(eq("refresh-token"), any(LocalDateTime.class));
    }

    @Test
    void testRefreshToken_WhenExceptionOccurs_ShouldThrowAuthenticationException() {
        when(refreshTokenRepository.findValidByToken(eq("refresh-token"), any(LocalDateTime.class)))
            .thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class, () -> authService.refreshToken(refreshTokenRequest));
        verify(refreshTokenRepository).findValidByToken(eq("refresh-token"), any(LocalDateTime.class));
    }
}
