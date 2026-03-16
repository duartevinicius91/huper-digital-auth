package huper.digital.iam.service;

import huper.digital.iam.dto.LoginRequest;
import huper.digital.iam.dto.RefreshTokenRequest;
import huper.digital.iam.dto.TokenResponse;
import huper.digital.iam.exception.AuthenticationException;
import huper.digital.iam.repository.AuthRefreshTokenRepository;
import huper.digital.iam.repository.AuthUserRepository;
import huper.digital.iam.security.AuthAccessService;
import huper.digital.iam.security.JwtTokenService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@ApplicationScoped
@Slf4j
public class AuthService {

  @Inject
  AuthUserRepository authUserRepository;

  @Inject
  AuthRefreshTokenRepository refreshTokenRepository;

  @Inject
  AuthAccessService accessService;

  @Inject
  JwtTokenService jwtTokenService;

  @Transactional
  public TokenResponse authenticate(LoginRequest request) {
    var user = authUserRepository.findByEmail(request.email())
        .orElseThrow(() -> new AuthenticationException("Credenciais inválidas"));

    if (Boolean.FALSE.equals(user.getEnabled())) {
      throw new AuthenticationException("Usuário desativado");
    }

    if (!jwtTokenService.matchesPassword(request.password(), user.getPasswordHash())) {
      throw new AuthenticationException("Credenciais inválidas");
    }

    var accessToken = jwtTokenService.generateAccessToken(user, accessService.resolveRoleNames(user));
    var refreshToken = jwtTokenService.createAndPersistRefreshToken(user, refreshTokenRepository);

    return new TokenResponse(accessToken, refreshToken, jwtTokenService.accessTokenTtlSeconds());
  }

  @Transactional
  public TokenResponse refreshToken(@Valid RefreshTokenRequest tokenRequest) {
    var now = LocalDateTime.now();
    var refresh = refreshTokenRepository.findValidByToken(tokenRequest.refreshToken(), now)
        .orElseThrow(() -> new AuthenticationException("Refresh token inválido ou expirado"));

    var user = refresh.getUser();
    if (Boolean.FALSE.equals(user.getEnabled())) {
      throw new AuthenticationException("Usuário desativado");
    }

    // Rotate token
    refresh.setRevoked(true);
    refreshTokenRepository.persist(refresh);

    var accessToken = jwtTokenService.generateAccessToken(user, accessService.resolveRoleNames(user));
    var newRefreshToken = jwtTokenService.createAndPersistRefreshToken(user, refreshTokenRepository);

    return new TokenResponse(accessToken, newRefreshToken, jwtTokenService.accessTokenTtlSeconds());
  }
}

