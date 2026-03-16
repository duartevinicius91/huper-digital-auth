package huper.digital.iam.permission.service;

import huper.digital.iam.permission.dto.PermissionNodeDTO;
import huper.digital.iam.permission.entity.PermissionEntity;
import huper.digital.iam.permission.repository.PermissionRepository;
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
  PermissionRepository permissionRepository;

  public PermissionEntity getRoot() {
    return permissionRepository.findRoot()
        .orElseThrow(() -> new NotFoundException("Permission tree root not found"));
  }

  public List<String> getAllPermissions() {
    List<String> permissions = new ArrayList<>();
    collectPermissions(getRoot(), permissions);
    return permissions;
  }

  private void collectPermissions(PermissionEntity permission, List<String> permissions) {
    if (permission.getPermissionConstant() != null) {
      permissions.add(permission.getPermissionConstant());
    }
    for (PermissionEntity child : permission.getChildren()) {
      collectPermissions(child, permissions);
    }
  }

  public PermissionEntity findByPermissionConstant(String permissionConstant) {
    return permissionRepository.findByPermissionConstant(permissionConstant).orElse(null);
  }

  public String getDescription(String role) {
    PermissionEntity permission = findByPermissionConstant(role);
    return permission != null ? permission.getDescription() : role;
  }

  public Map<String, String> getAllDescriptions() {
    Map<String, String> descriptions = new HashMap<>();
    buildDescriptionsMap(getRoot(), descriptions);
    return descriptions;
  }

  private void buildDescriptionsMap(PermissionEntity permission, Map<String, String> descriptions) {
    if (permission.getPermissionConstant() != null) {
      descriptions.put(permission.getPermissionConstant(), permission.getDescription());
    }
    for (PermissionEntity child : permission.getChildren()) {
      buildDescriptionsMap(child, descriptions);
    }
  }

  public PermissionEntity getPermissionTree() {
    PermissionEntity root = getRoot();
    loadChildrenRecursively(root);
    return root;
  }

  private void loadChildrenRecursively(PermissionEntity permission) {
    permission.getChildren().size();
    for (PermissionEntity child : permission.getChildren()) {
      loadChildrenRecursively(child);
    }
  }

  public PermissionNodeDTO toDto(PermissionEntity permission) {
    List<PermissionNodeDTO> children = permission.getChildren().stream()
        .sorted((a, b) -> Integer.compare(a.getSortOrder(), b.getSortOrder()))
        .map(this::toDto)
        .collect(Collectors.toList());
    return new PermissionNodeDTO(
        permission.getId(), permission.getName(), permission.getDescription(),
        permission.getPermissionConstant(), children, permission.getSortOrder()
    );
  }

  public PermissionNodeDTO getPermissionTreeDto() {
    return toDto(getPermissionTree());
  }

  public PermissionEntity findById(Long id) {
    return permissionRepository.findByIdOptional(id)
        .orElseThrow(() -> new NotFoundException("Permissão não encontrada: " + id));
  }

  public List<PermissionEntity> findAll() {
    return permissionRepository.listAll();
  }
}
