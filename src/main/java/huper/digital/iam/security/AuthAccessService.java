package huper.digital.iam.security;

import huper.digital.iam.groups.entity.GroupEntity;
import huper.digital.iam.permission.entity.PermissionEntity;
import huper.digital.iam.user.entity.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@ApplicationScoped
public class AuthAccessService {

  public Set<String> resolveRoleNames(UserEntity user) {
    Set<String> roles = new HashSet<>();
    if (user == null) return roles;
    if (user.getGroups() == null) return roles;
    for (GroupEntity g : user.getGroups()) {
      if (g == null) continue;
      if (g.getRoleNames() != null) {
        g.getRoleNames().stream().filter(n -> n != null && !n.isBlank()).forEach(roles::add);
      }
      if (g.getPermissions() != null) {
        g.getPermissions().stream()
            .map(PermissionEntity::getPermissionConstant)
            .filter(Objects::nonNull)
            .forEach(roles::add);
      }
    }
    return roles;
  }
}
