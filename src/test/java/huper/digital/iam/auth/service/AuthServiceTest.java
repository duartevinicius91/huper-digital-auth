package huper.digital.iam.auth.service;

import huper.digital.iam.auth.dto.LoginRequest;
import huper.digital.iam.auth.dto.RefreshTokenRequest;
import huper.digital.iam.auth.dto.TokenResponse;
import huper.digital.iam.common.exception.AuthenticationException;
import huper.digital.iam.security.AuthAccessService;
import huper.digital.iam.security.JwtTokenService;
import huper.digital.iam.user.entity.RefreshTokenEntity;
import huper.digital.iam.user.entity.UserEntity;
import huper.digital.iam.user.repository.RefreshTokenRepository;
import huper.digital.iam.user.repository.UserRepository;
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
  private UserRepository userRepository;
  @Mock
  private RefreshTokenRepository refreshTokenRepository;
  @Mock
  private AuthAccessService accessService;
  @Mock
  private JwtTokenService jwtTokenService;
  @InjectMocks
  private AuthService authService;

  private LoginRequest loginRequest;
  private RefreshTokenRequest refreshTokenRequest;
  private UserEntity user;

  @BeforeEach
  void setUp() {
    loginRequest = new LoginRequest("test@example.com", "password");
    refreshTokenRequest = new RefreshTokenRequest("refresh-token");
    user = new UserEntity();
    user.setId(1L);
    user.setEmail("test@example.com");
    user.setEnabled(true);
    user.setPasswordHash("$2a$10$hash");
  }

  @Test
  void testAuthenticate_ShouldReturnTokenResponse() {
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
    when(jwtTokenService.matchesPassword("password", "$2a$10$hash")).thenReturn(true);
    when(accessService.resolveRoleNames(user)).thenReturn(Set.of("USER_MANAGEMENT_READ"));
    when(jwtTokenService.generateAccessToken(eq(user), anySet())).thenReturn("access-token");
    when(jwtTokenService.createAndPersistRefreshToken(eq(user), eq(refreshTokenRepository))).thenReturn("refresh-token");
    when(jwtTokenService.accessTokenTtlSeconds()).thenReturn(3600L);

    TokenResponse result = authService.authenticate(loginRequest);

    assertNotNull(result);
    assertEquals("access-token", result.accessToken());
    verify(userRepository).findByEmail("test@example.com");
  }

  @Test
  void testAuthenticate_WhenExceptionOccurs_ShouldThrowAuthenticationException() {
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
    assertThrows(AuthenticationException.class, () -> authService.authenticate(loginRequest));
  }

  @Test
  void testRefreshToken_ShouldReturnTokenResponse() {
    RefreshTokenEntity refresh = new RefreshTokenEntity();
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
  }

  @Test
  void testRefreshToken_WhenInvalid_ShouldThrowAuthenticationException() {
    when(refreshTokenRepository.findValidByToken(eq("refresh-token"), any(LocalDateTime.class)))
        .thenReturn(Optional.empty());
    assertThrows(AuthenticationException.class, () -> authService.refreshToken(refreshTokenRequest));
  }
}
