package huper.digital.iam.repository;

import huper.digital.iam.entity.AuthUserEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class AuthUserRepository implements PanacheRepositoryBase<AuthUserEntity, Long> {

  public Optional<AuthUserEntity> findByEmail(String email) {
    if (email == null) {
      return Optional.empty();
    }
    return find("LOWER(email) = LOWER(?1)", email).firstResultOptional();
  }

}

