package huper.digital.iam.tenant.repository;

import huper.digital.iam.tenant.entity.TenantEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class TenantRepository implements PanacheRepositoryBase<TenantEntity, Long> {

  public Optional<TenantEntity> findByName(String name) {
    if (name == null) return Optional.empty();
    return find("LOWER(name) = LOWER(?1)", name).firstResultOptional();
  }
}
