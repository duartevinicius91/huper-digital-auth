package huper.digital.iam.repository;

import huper.digital.iam.entity.AuthRefreshTokenEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.Optional;

@ApplicationScoped
public class AuthRefreshTokenRepository implements PanacheRepository<AuthRefreshTokenEntity> {

  public Optional<AuthRefreshTokenEntity> findValidByToken(String token, LocalDateTime now) {
    if (token == null) {
      return Optional.empty();
    }
    return find("token = ?1 and revoked = false and expiresAt > ?2", token, now).firstResultOptional();
  }
}

