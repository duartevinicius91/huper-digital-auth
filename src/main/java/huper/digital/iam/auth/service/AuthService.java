package huper.digital.iam.auth.service;

import huper.digital.iam.auth.dto.LoginRequest;
import huper.digital.iam.auth.dto.RefreshTokenRequest;
import huper.digital.iam.auth.dto.TokenResponse;
import huper.digital.iam.common.exception.AuthenticationException;
import huper.digital.iam.security.AuthAccessService;
import huper.digital.iam.security.JwtTokenService;
import huper.digital.iam.user.repository.RefreshTokenRepository;
import huper.digital.iam.user.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

@ApplicationScoped
public class AuthService {

  @Inject
  UserRepository userRepository;
  @Inject
  RefreshTokenRepository refreshTokenRepository;
  @Inject
  AuthAccessService accessService;
  @Inject
  JwtTokenService jwtTokenService;

  @Transactional
  public TokenResponse authenticate(LoginRequest request) {
    var user = userRepository.findByEmail(request.email())
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
  public TokenResponse refreshToken(RefreshTokenRequest tokenRequest) {
    var now = LocalDateTime.now();
    var refresh = refreshTokenRepository.findValidByToken(tokenRequest.refreshToken(), now)
        .orElseThrow(() -> new AuthenticationException("Refresh token inválido ou expirado"));
    var user = refresh.getUser();
    if (Boolean.FALSE.equals(user.getEnabled())) {
      throw new AuthenticationException("Usuário desativado");
    }
    refresh.setRevoked(true);
    refreshTokenRepository.persist(refresh);
    var accessToken = jwtTokenService.generateAccessToken(user, accessService.resolveRoleNames(user));
    var newRefreshToken = jwtTokenService.createAndPersistRefreshToken(user, refreshTokenRepository);
    return new TokenResponse(accessToken, newRefreshToken, jwtTokenService.accessTokenTtlSeconds());
  }
}
