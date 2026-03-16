package huper.digital.iam.service;

import huper.digital.iam.dto.PermissionNodeDTO;
import huper.digital.iam.entity.AuthPermissionEntity;
import huper.digital.iam.repository.AuthPermissionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
public class PermissionService {

  @Inject
  AuthPermissionRepository permissionRepository;

  /**
   * Get the permission tree root
   * @return The root permission entity
   */
  public AuthPermissionEntity getRoot() {
    return permissionRepository.findRoot()
        .orElseThrow(() -> new NotFoundException("Permission tree root not found"));
  }

  /**
   * Get all leaf permissions (actual role constants)
   * @return List of all permission constants
   */
  public List<String> getAllPermissions() {
    List<String> permissions = new ArrayList<>();
    collectPermissions(getRoot(), permissions);
    return permissions;
  }

  /**
   * Recursively collect all leaf permissions from the tree
   */
  private void collectPermissions(AuthPermissionEntity permission, List<String> permissions) {
    if (permission.getPermissionConstant() != null) {
      permissions.add(permission.getPermissionConstant());
    }
    for (AuthPermissionEntity child : permission.getChildren()) {
      collectPermissions(child, permissions);
    }
  }

  /**
   * Find a permission by its permission constant
   * @param permissionConstant The permission constant to find
   * @return The Permission entity if found, null otherwise
   */
  public AuthPermissionEntity findByPermissionConstant(String permissionConstant) {
    return permissionRepository.findByPermissionConstant(permissionConstant)
        .orElse(null);
  }

  /**
   * Get the description for a given role
   * @param role The role constant
   * @return The description of the role, or the role itself if no description is found
   */
  public String getDescription(String role) {
    AuthPermissionEntity permission = findByPermissionConstant(role);
    if (permission != null) {
      return permission.getDescription();
    }
    return role;
  }

  /**
   * Get all role descriptions
   * @return A map of all role descriptions
   */
  public Map<String, String> getAllDescriptions() {
    Map<String, String> descriptions = new HashMap<>();
    buildDescriptionsMap(getRoot(), descriptions);
    return descriptions;
  }

  /**
   * Recursively build the descriptions map from the permission tree
   */
  private void buildDescriptionsMap(AuthPermissionEntity permission, Map<String, String> descriptions) {
    if (permission.getPermissionConstant() != null) {
      descriptions.put(permission.getPermissionConstant(), permission.getDescription());
    }
    for (AuthPermissionEntity child : permission.getChildren()) {
      buildDescriptionsMap(child, descriptions);
    }
  }

  /**
   * Get permission tree as a hierarchical structure
   * @return The root permission with all children loaded
   */
  public AuthPermissionEntity getPermissionTree() {
    AuthPermissionEntity root = getRoot();
    // Ensure children are loaded recursively
    loadChildrenRecursively(root);
    return root;
  }

  /**
   * Recursively load all children to avoid lazy loading issues
   */
  private void loadChildrenRecursively(AuthPermissionEntity permission) {
    permission.getChildren().size(); // Trigger lazy loading
    for (AuthPermissionEntity child : permission.getChildren()) {
      loadChildrenRecursively(child);
    }
  }

  /**
   * Convert permission entity to DTO recursively
   */
  public PermissionNodeDTO toDto(AuthPermissionEntity permission) {
    List<PermissionNodeDTO> children = permission.getChildren().stream()
        .sorted((a, b) -> Integer.compare(a.getSortOrder(), b.getSortOrder()))
        .map(this::toDto)
        .collect(Collectors.toList());

    return new PermissionNodeDTO(
        permission.getId(),
        permission.getName(),
        permission.getDescription(),
        permission.getPermissionConstant(),
        children,
        permission.getSortOrder()
    );
  }

  /**
   * Get permission tree as DTO
   */
  public PermissionNodeDTO getPermissionTreeDto() {
    return toDto(getPermissionTree());
  }
}

