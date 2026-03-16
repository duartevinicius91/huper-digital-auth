package huper.digital.iam.groups.service;

import huper.digital.iam.groups.dto.GroupDTO;
import huper.digital.iam.groups.entity.GroupEntity;
import huper.digital.iam.groups.repository.GroupRepository;
import huper.digital.iam.permission.entity.PermissionEntity;
import huper.digital.iam.permission.service.PermissionService;
import huper.digital.iam.tenant.entity.TenantEntity;
import huper.digital.iam.tenant.repository.TenantRepository;
import huper.digital.iam.user.dto.UserDTO;
import huper.digital.iam.user.entity.UserEntity;
import huper.digital.iam.user.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
public class GroupService {

  @Inject
  GroupRepository groupRepository;
  @Inject
  UserRepository userRepository;
  @Inject
  TenantRepository tenantRepository;
  @Inject
  PermissionService permissionService;

  public List<GroupDTO> findAll(Integer first, Integer max, String search, Long organizationId) {
    int safeFirst = first == null ? 0 : Math.max(0, first);
    int safeMax = max == null ? 10 : Math.max(1, max);
    int last = safeFirst + safeMax - 1;
    var query = organizationId != null
        ? (StringUtils.isBlank(search)
            ? groupRepository.find("organization.id = ?1", organizationId)
            : groupRepository.find("organization.id = ?1 AND LOWER(name) LIKE ?2", organizationId, "%" + search.toLowerCase() + "%"))
        : (StringUtils.isBlank(search)
            ? groupRepository.findAll()
            : groupRepository.find("LOWER(name) LIKE ?1", "%" + search.toLowerCase() + "%"));
    return query.range(safeFirst, last).list().stream().map(g -> toDto(g, true)).toList();
  }

  public List<GroupDTO> findAll(Integer first, Integer max, String search) {
    return findAll(first, max, search, null);
  }

  public List<GroupDTO> findAll() {
    return findAll(0, 1000, null);
  }

  public GroupDTO findById(Long groupId) {
    GroupEntity entity = groupRepository.findByIdOptional(groupId)
        .orElseThrow(() -> new NotFoundException("Grupo não encontrado: " + groupId));
    return toDto(entity, true);
  }

  public Optional<GroupDTO> findByName(String name) {
    return groupRepository.findByName(name).map(g -> toDto(g, false));
  }

  @Transactional
  public GroupDTO create(GroupDTO groupDTO) {
    findByName(groupDTO.name()).ifPresent(g -> {
      throw new IllegalArgumentException("Já existe um grupo com o nome: " + groupDTO.name());
    });
    GroupEntity entity = new GroupEntity();
    entity.setName(groupDTO.name());
    if (groupDTO.organizationId() != null) {
      TenantEntity org = tenantRepository.findByIdOptional(groupDTO.organizationId())
          .orElseThrow(() -> new NotFoundException("Tenant não encontrado: " + groupDTO.organizationId()));
      entity.setOrganization(org);
    }
    if (groupDTO.realmRoles() != null) {
      groupDTO.realmRoles().stream().filter(r -> r != null && !r.isBlank()).forEach(entity.getRoleNames()::add);
    }
    if (groupDTO.permissionIds() != null) {
      for (Long permId : groupDTO.permissionIds()) {
        PermissionEntity p = permissionService.findById(permId);
        entity.getPermissions().add(p);
      }
    }
    groupRepository.persist(entity);
    return toDto(entity, true);
  }

  @Transactional
  public GroupDTO update(Long groupId, GroupDTO groupDTO) {
    GroupEntity entity = groupRepository.findByIdOptional(groupId)
        .orElseThrow(() -> new NotFoundException("Grupo não encontrado: " + groupId));
    if (StringUtils.isNotBlank(groupDTO.name()) && !entity.getName().equalsIgnoreCase(groupDTO.name())) {
      groupRepository.findByName(groupDTO.name()).ifPresent(g -> {
        if (!g.getId().equals(groupId)) throw new IllegalArgumentException("Já existe outro grupo com o nome: " + groupDTO.name());
      });
      entity.setName(groupDTO.name());
    }
    if (groupDTO.organizationId() != null) {
      entity.setOrganization(tenantRepository.findByIdOptional(groupDTO.organizationId()).orElse(null));
    }
    if (groupDTO.realmRoles() != null) {
      entity.getRoleNames().clear();
      groupDTO.realmRoles().stream().filter(r -> r != null && !r.isBlank()).forEach(entity.getRoleNames()::add);
    }
    if (groupDTO.permissionIds() != null) {
      entity.getPermissions().clear();
      for (Long permId : groupDTO.permissionIds()) {
        entity.getPermissions().add(permissionService.findById(permId));
      }
    }
    groupRepository.persist(entity);
    return toDto(entity, true);
  }

  @Transactional
  public void delete(Long groupId) {
    GroupEntity entity = groupRepository.findByIdOptional(groupId)
        .orElseThrow(() -> new NotFoundException("Grupo não encontrado: " + groupId));
    if (Boolean.TRUE.equals(entity.getIsDefault())) {
      throw new IllegalStateException("Não é possível excluir o grupo padrão.");
    }
    groupRepository.delete(entity);
  }

  public List<UserDTO> getGroupMembers(Long groupId, Integer first, Integer max) {
    if (!groupRepository.findByIdOptional(groupId).isPresent()) {
      throw new NotFoundException("Grupo não encontrado: " + groupId);
    }
    int safeFirst = first == null ? 0 : Math.max(0, first);
    int safeMax = max == null ? 10 : Math.max(1, max);
    List<UserEntity> users = userRepository.find(
        "select u from UserEntity u join u.groups g where g.id = ?1", groupId)
        .range(safeFirst, safeFirst + safeMax - 1).list();
    return users.stream().map(this::toUserDto).toList();
  }

  public List<UserDTO> getGroupMembers(Long groupId) {
    return getGroupMembers(groupId, 0, 1000);
  }

  @Transactional
  public void addMember(Long groupId, Long userId) {
    GroupEntity group = groupRepository.findByIdOptional(groupId)
        .orElseThrow(() -> new NotFoundException("Grupo não encontrado: " + groupId));
    UserEntity user = userRepository.findByIdOptional(userId)
        .orElseThrow(() -> new NotFoundException("Usuário não encontrado: " + userId));
    user.getGroups().add(group);
    userRepository.persist(user);
  }

  @Transactional
  public void removeMember(Long groupId, Long userId) {
    GroupEntity group = groupRepository.findByIdOptional(groupId)
        .orElseThrow(() -> new NotFoundException("Grupo não encontrado: " + groupId));
    UserEntity user = userRepository.findByIdOptional(userId)
        .orElseThrow(() -> new NotFoundException("Usuário não encontrado: " + userId));
    user.getGroups().remove(group);
    userRepository.persist(user);
  }

  public List<String> getGroupPermissionConstants(Long groupId) {
    GroupEntity group = groupRepository.findByIdOptional(groupId)
        .orElseThrow(() -> new NotFoundException("Grupo não encontrado: " + groupId));
    Set<String> roles = new java.util.HashSet<>(group.getRoleNames() != null ? group.getRoleNames() : Set.of());
    if (group.getPermissions() != null) {
      group.getPermissions().stream()
          .map(PermissionEntity::getPermissionConstant)
          .filter(Objects::nonNull)
          .forEach(roles::add);
    }
    return new ArrayList<>(roles);
  }

  @Transactional
  public void assignPermission(Long groupId, Long permissionId) {
    GroupEntity group = groupRepository.findByIdOptional(groupId)
        .orElseThrow(() -> new NotFoundException("Grupo não encontrado: " + groupId));
    PermissionEntity permission = permissionService.findById(permissionId);
    group.getPermissions().add(permission);
    groupRepository.persist(group);
  }

  @Transactional
  public void removePermission(Long groupId, Long permissionId) {
    GroupEntity group = groupRepository.findByIdOptional(groupId)
        .orElseThrow(() -> new NotFoundException("Grupo não encontrado: " + groupId));
    PermissionEntity permission = permissionService.findById(permissionId);
    group.getPermissions().remove(permission);
    groupRepository.persist(group);
  }

  public boolean exists(Long groupId) {
    return groupRepository.findByIdOptional(groupId).isPresent();
  }

  private GroupDTO toDto(GroupEntity group, boolean includeMembers) {
    List<String> roleNames = group.getRoleNames() == null ? List.of() : new ArrayList<>(group.getRoleNames());
    List<Long> permissionIds = group.getPermissions() == null ? List.of()
        : group.getPermissions().stream().map(PermissionEntity::getId).collect(Collectors.toList());
    List<String> members = includeMembers
        ? userRepository.find("select u from UserEntity u join u.groups g where g.id = ?1", group.getId())
        .list().stream().map(u -> String.valueOf(u.getId())).toList()
        : null;
    Long orgId = group.getOrganization() != null ? group.getOrganization().getId() : null;
    return new GroupDTO(group.getId(), group.getName(), orgId, group.getIsDefault(), roleNames, permissionIds, members);
  }

  private UserDTO toUserDto(UserEntity user) {
    List<String> groupIds = user.getGroups() == null ? List.of()
        : user.getGroups().stream().map(g -> String.valueOf(g.getId())).toList();
    Set<String> roleNames = new java.util.HashSet<>();
    if (user.getGroups() != null) {
      user.getGroups().forEach(g -> {
        if (g.getRoleNames() != null) g.getRoleNames().forEach(roleNames::add);
        if (g.getPermissions() != null) {
          g.getPermissions().stream().map(PermissionEntity::getPermissionConstant).filter(Objects::nonNull).forEach(roleNames::add);
        }
      });
    }
    return new UserDTO(user.getId(), user.getEmail(), user.getPhone(), user.getFirstName(), user.getLastName(),
        user.getBirthDate(), user.getTaxIdentifier(), user.getAddress(), user.getEnabled(), user.getStatus(),
        groupIds, new ArrayList<>(roleNames));
  }
}
