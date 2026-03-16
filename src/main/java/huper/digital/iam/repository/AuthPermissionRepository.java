package huper.digital.iam.repository;

import huper.digital.iam.entity.AuthPermissionEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class AuthPermissionRepository implements PanacheRepositoryBase<AuthPermissionEntity, Long> {

  public Optional<AuthPermissionEntity> findByPermissionConstant(String permissionConstant) {
    if (permissionConstant == null) {
      return Optional.empty();
    }
    return find("permissionConstant = ?1", permissionConstant).firstResultOptional();
  }

  public Optional<AuthPermissionEntity> findRoot() {
    return find("parent IS NULL").firstResultOptional();
  }
}

