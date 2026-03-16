package huper.digital.iam.security;

import huper.digital.iam.entity.AuthRefreshTokenEntity;
import huper.digital.iam.entity.AuthUserEntity;
import huper.digital.iam.repository.AuthRefreshTokenRepository;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Base64;
import java.util.Set;

@ApplicationScoped
public class JwtTokenService {

  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "huper.digital")
  String issuer;

  @ConfigProperty(name = "huper.digital.iam.jwt.access-token-ttl-seconds", defaultValue = "3600")
  long accessTokenTtlSeconds;

  @ConfigProperty(name = "huper.digital.iam.jwt.refresh-token-ttl-seconds", defaultValue = "2592000")
  long refreshTokenTtlSeconds;

  public long accessTokenTtlSeconds() {
    return accessTokenTtlSeconds;
  }

  public boolean matchesPassword(String rawPassword, String passwordHash) {
    if (rawPassword == null || passwordHash == null) {
      return false;
    }
    return BCrypt.checkpw(rawPassword, passwordHash);
  }

  public String hashPassword(String rawPassword) {
    return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
  }

  public String generateAccessToken(AuthUserEntity user, Set<String> roleNames) {
    Instant now = Instant.now();
    Instant expiresAt = now.plusSeconds(accessTokenTtlSeconds);

    List<String> organizationIds = user.getOrganizationMemberships() == null
        ? List.of()
        : user.getOrganizationMemberships().stream()
        .filter(m -> m != null && m.getOrganization() != null)
        .map(m -> m.getOrganization().getId())
        .map(String::valueOf)
        .distinct()
        .toList();

    List<String> ownerOrganizationIds = user.getOrganizationMemberships() == null
        ? List.of()
        : user.getOrganizationMemberships().stream()
        .filter(m -> m != null && Boolean.TRUE.equals(m.getOwner()) && m.getOrganization() != null)
        .map(m -> m.getOrganization().getId())
        .map(String::valueOf)
        .distinct()
        .toList();

    // Backward-compat: keep a single organizationId claim when possible
    String organizationId = !ownerOrganizationIds.isEmpty()
        ? ownerOrganizationIds.getFirst()
        : (organizationIds.isEmpty() ? null : organizationIds.getFirst());

    return Jwt.issuer(issuer)
        .upn(user.getEmail())
        .subject(user.getId().toString())
        .issuedAt(now)
        .expiresAt(expiresAt)
        .groups(roleNames == null ? Set.of() : roleNames)
        .claim("userId", user.getId().toString())
        .claim("organizationId", "organizationId")
        .claim("organizationIds", organizationIds)
        .claim("ownerOrganizationIds", ownerOrganizationIds)
        .sign();
  }

  public String createAndPersistRefreshToken(AuthUserEntity user, AuthRefreshTokenRepository repository) {
    byte[] bytes = new byte[32];
    SECURE_RANDOM.nextBytes(bytes);
    String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

    AuthRefreshTokenEntity entity = new AuthRefreshTokenEntity();
    entity.setUser(user);
    entity.setToken(token);
    entity.setExpiresAt(LocalDateTime.ofInstant(Instant.now().plusSeconds(refreshTokenTtlSeconds), ZoneOffset.UTC));
    entity.setRevoked(false);

    repository.persist(entity);
    return token;
  }
}

