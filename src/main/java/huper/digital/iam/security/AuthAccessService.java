package huper.digital.iam.security;

import huper.digital.iam.entity.AuthUserEntity;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class AuthAccessService {

  public Set<String> resolveRoleNames(AuthUserEntity user) {
    Set<String> roles = new HashSet<>();

    if (user == null) {
      return roles;
    }

    if (user.getGroups() != null) {
      user.getGroups().forEach(g -> {
        if (g == null || g.getRoleNames() == null) {
          return;
        }
        g.getRoleNames().stream()
            .filter(n -> n != null && !n.isBlank())
            .forEach(roles::add);
      });
    }

    return roles;
  }
}

