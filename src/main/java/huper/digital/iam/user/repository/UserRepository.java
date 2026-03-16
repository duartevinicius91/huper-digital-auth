package huper.digital.iam.user.repository;

import huper.digital.iam.user.entity.UserEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<UserEntity, Long> {

  public Optional<UserEntity> findByEmail(String email) {
    if (email == null) return Optional.empty();
    return find("LOWER(email) = LOWER(?1)", email).firstResultOptional();
  }
}
