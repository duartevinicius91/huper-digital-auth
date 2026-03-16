package huper.digital.iam.user.repository;

import huper.digital.iam.user.entity.RefreshTokenEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.Optional;

@ApplicationScoped
public class RefreshTokenRepository implements PanacheRepositoryBase<RefreshTokenEntity, Long> {

  public Optional<RefreshTokenEntity> findByToken(String token) {
    if (token == null) return Optional.empty();
    return find("token = ?1", token).firstResultOptional();
  }

  public Optional<RefreshTokenEntity> findValidByToken(String token, LocalDateTime now) {
    if (token == null) return Optional.empty();
    return find("token = ?1 and revoked = false and expiresAt > ?2", token, now).firstResultOptional();
  }
}
