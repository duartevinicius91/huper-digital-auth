package huper.digital.iam.groups.repository;

import huper.digital.iam.groups.entity.GroupEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class GroupRepository implements PanacheRepositoryBase<GroupEntity, Long> {

  public Optional<GroupEntity> findByName(String name) {
    if (name == null) return Optional.empty();
    return find("LOWER(name) = LOWER(?1)", name).firstResultOptional();
  }

  public List<GroupEntity> findByOrganizationId(Long organizationId) {
    if (organizationId == null) return List.of();
    return find("organization.id = ?1", organizationId).list();
  }
}
