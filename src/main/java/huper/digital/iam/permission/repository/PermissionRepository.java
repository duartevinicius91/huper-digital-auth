package huper.digital.iam.permission.repository;

import huper.digital.iam.permission.entity.PermissionEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class PermissionRepository implements PanacheRepositoryBase<PermissionEntity, Long> {

  public Optional<PermissionEntity> findByPermissionConstant(String permissionConstant) {
    if (permissionConstant == null) return Optional.empty();
    return find("permissionConstant = ?1", permissionConstant).firstResultOptional();
  }

  public Optional<PermissionEntity> findRoot() {
    return find("parent IS NULL").firstResultOptional();
  }
}
