package huper.digital.iam.repository;

import huper.digital.iam.entity.AuthOrganizationEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class AuthOrganizationRepository implements PanacheRepositoryBase<AuthOrganizationEntity, Long> {

  public Optional<AuthOrganizationEntity> findByName(String name) {
    if (name == null) {
      return Optional.empty();
    }
    return find("LOWER(name) = LOWER(?1)", name).firstResultOptional();
  }
}

