package huper.digital.iam.repository;

import huper.digital.iam.entity.AuthGroupEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AuthGroupRepository implements PanacheRepositoryBase<AuthGroupEntity, Long> {

  public Optional<AuthGroupEntity> findByName(String name) {
    if (name == null) {
      return Optional.empty();
    }
    return find("LOWER(name) = LOWER(?1)", name).firstResultOptional();
  }

  public List<AuthGroupEntity> findByOrganizationId(Long organizationId) {
    if (organizationId == null) {
      return List.of();
    }
    return find("organization.id = ?1", organizationId).list();
  }
}

