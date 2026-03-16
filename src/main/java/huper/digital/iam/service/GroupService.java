package huper.digital.iam.service;


import huper.digital.iam.dto.GroupDTO;
import huper.digital.iam.dto.UserDTO;
import huper.digital.iam.entity.AuthGroupEntity;
import huper.digital.iam.entity.AuthOrganizationEntity;
import huper.digital.iam.entity.AuthUserEntity;
import huper.digital.iam.repository.AuthGroupRepository;
import huper.digital.iam.repository.AuthOrganizationRepository;
import huper.digital.iam.repository.AuthUserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Slf4j
public class GroupService {

  @Inject
  AuthGroupRepository groupRepository;

  @Inject
  AuthUserRepository userRepository;

  @Inject
  AuthOrganizationRepository organizationRepository;


  public List<GroupDTO> findAll(Integer first, Integer max, String search, Long organizationId) {
    try {
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

      List<GroupDTO> groups = query.range(safeFirst, last).list().stream()
          .map(g -> toDto(g, true)) // Include members to show count
          .toList();

      log.debug("Listados {} grupos", groups.size());
      return groups;
    } catch (Exception e) {
      log.error("Erro ao listar grupos: {}", e.getMessage(), e);
      throw new RuntimeException("Falha ao listar grupos", e);
    }
  }

  public List<GroupDTO> findAll(Integer first, Integer max, String search) {
    return findAll(first, max, search, null);
  }

  public List<GroupDTO> findAll() {
    return findAll(0, 1000, null);
  }

  public GroupDTO findById(Long groupId) {
    AuthGroupEntity entity = groupRepository.findByIdOptional(groupId)
        .orElseThrow(() -> new NotFoundException("Grupo não encontrado: " + groupId));
    return toDto(entity, true);
  }

  public Optional<GroupDTO> findByName(String name) {
    return groupRepository.findByName(name).map(g -> toDto(g, false));
  }

  @Transactional
  public GroupDTO create(GroupDTO groupDTO) {
    try {
      Optional<GroupDTO> existingGroup = findByName(groupDTO.name());
      if (existingGroup.isPresent()) {
        throw new IllegalArgumentException("Já existe um grupo com o nome: " + groupDTO.name());
      }

      AuthGroupEntity entity = new AuthGroupEntity();
      entity.setName(groupDTO.name());

      // Associate with organization if provided
      if (groupDTO.organizationId() != null) {
        AuthOrganizationEntity organization = organizationRepository.findByIdOptional(groupDTO.organizationId())
            .orElseThrow(() -> new NotFoundException("Organização não encontrada: " + groupDTO.organizationId()));
        entity.setOrganization(organization);
      }

      // Allow creating with initial roles by name
      if (groupDTO.realmRoles() != null && !groupDTO.realmRoles().isEmpty()) {
        groupDTO.realmRoles().stream()
            .filter(r -> r != null && !r.isBlank())
            .forEach(entity.getRoleNames()::add);
      }

      groupRepository.persist(entity);
      log.info("Grupo criado com sucesso: {}", groupDTO.name());
      return toDto(entity, true);

    } catch (Exception e) {
      log.error("Erro ao criar grupo {}: {}", groupDTO.name(), e.getMessage(), e);
      throw new RuntimeException("Falha ao criar grupo", e);
    }
  }

  @Transactional
  public GroupDTO update(Long groupId, GroupDTO groupDTO) {
    try {
      AuthGroupEntity entity = groupRepository.findByIdOptional(groupId)
          .orElseThrow(() -> new NotFoundException("Grupo não encontrado: " + groupId));

      if (StringUtils.isNotBlank(groupDTO.name()) && !entity.getName().equalsIgnoreCase(groupDTO.name())) {
        var groupWithSameName = groupRepository.findByName(groupDTO.name());
        if (groupWithSameName.isPresent() && !groupWithSameName.get().getId().equals(groupId)) {
          throw new IllegalArgumentException("Já existe outro grupo com o nome: " + groupDTO.name());
        }
      }

      if (StringUtils.isNotBlank(groupDTO.name())) {
        entity.setName(groupDTO.name());
      }
      groupRepository.persist(entity);
      log.info("Grupo atualizado com sucesso: {}", groupId);
      return toDto(entity, true);

    } catch (Exception e) {
      log.error("Erro ao atualizar grupo {}: {}", groupId, e.getMessage(), e);
      throw new RuntimeException("Falha ao atualizar grupo", e);
    }
  }

  @Transactional
  public void delete(Long groupId) {
    try {
      AuthGroupEntity entity = groupRepository.findByIdOptional(groupId)
          .orElseThrow(() -> new NotFoundException("Grupo não encontrado: " + groupId));

      // Prevent deletion of default group
      if (Boolean.TRUE.equals(entity.getIsDefault())) {
        throw new IllegalStateException("Não é possível excluir o grupo padrão.");
      }

      // Allow deletion even if group has members - members will be automatically removed
      groupRepository.delete(entity);
      log.info("Grupo removido com sucesso: {}", groupId);

    } catch (Exception e) {
      log.error("Erro ao remover grupo {}: {}", groupId, e.getMessage(), e);
      throw new RuntimeException("Falha ao remover grupo", e);
    }
  }


  public List<UserDTO> getGroupMembers(Long groupId, Integer first, Integer max) {
    try {
      if (!exists(groupId)) {
        throw new NotFoundException("Grupo não encontrado: " + groupId);
      }

      int safeFirst = first == null ? 0 : Math.max(0, first);
      int safeMax = max == null ? 10 : Math.max(1, max);
      int last = safeFirst + safeMax - 1;

      List<AuthUserEntity> users = userRepository.find(
              "select u from AuthUserEntity u join u.groups g where g.id = ?1",
              groupId
          )
          .range(safeFirst, last)
          .list();

      List<UserDTO> members = users.stream()
          .map(this::toUserDto)
          .toList();

      log.debug("Listados {} membros do grupo {}", members.size(), groupId);
      return members;

    } catch (Exception e) {
      log.error("Erro ao listar membros do grupo {}: {}", groupId, e.getMessage(), e);
      throw new RuntimeException("Falha ao listar membros do grupo", e);
    }
  }

  public List<UserDTO> getGroupMembers(Long groupId) {
    return getGroupMembers(groupId, 0, 1000);
  }

  @Transactional
  public void addMember(Long groupId, Long userId) {
    try {
      AuthGroupEntity group = groupRepository.findByIdOptional(groupId)
          .orElseThrow(() -> new NotFoundException("Grupo não encontrado: " + groupId));
      AuthUserEntity user = userRepository.findByIdOptional(userId)
          .orElseThrow(() -> new NotFoundException("Usuário não encontrado: " + userId));

      user.getGroups().add(group);
      userRepository.persist(user);
      log.info("Usuário {} adicionado ao grupo {}", userId, groupId);

    } catch (Exception e) {
      log.error("Erro ao adicionar usuário {} ao grupo {}: {}", userId, groupId, e.getMessage(), e);
      throw new RuntimeException("Falha ao adicionar usuário ao grupo", e);
    }
  }

  @Transactional
  public void removeMember(Long groupId, Long userId) {
    try {
      AuthGroupEntity group = groupRepository.findByIdOptional(groupId)
          .orElseThrow(() -> new NotFoundException("Grupo não encontrado: " + groupId));
      AuthUserEntity user = userRepository.findByIdOptional(userId)
          .orElseThrow(() -> new NotFoundException("Usuário não encontrado: " + userId));

      user.getGroups().remove(group);
      userRepository.persist(user);
      log.info("Usuário {} removido do grupo {}", userId, groupId);

    } catch (Exception e) {
      log.error("Erro ao remover usuário {} do grupo {}: {}", userId, groupId, e.getMessage(), e);
      throw new RuntimeException("Falha ao remover usuário do grupo", e);
    }
  }

  public boolean isMember(Long groupId, Long userId) {
    try {
      List<UserDTO> members = getGroupMembers(groupId);
      return members.stream()
          .anyMatch(member -> member.id().equals(userId));
    } catch (Exception e) {
      log.error("Erro ao verificar se usuário {} pertence ao grupo {}: {}", userId, groupId, e.getMessage(), e);
      return false;
    }
  }


  public List<String> getGroupPermissions(Long groupId) {
    try {
      AuthGroupEntity group = groupRepository.findByIdOptional(groupId)
          .orElseThrow(() -> new NotFoundException("Grupo não encontrado: " + groupId));

      List<String> permissions = group.getRoleNames() == null ? List.of() : group.getRoleNames().stream().toList();

      log.debug("Listadas {} permissões do grupo {}", permissions.size(), groupId);
      return permissions;

    } catch (Exception e) {
      log.error("Erro ao listar permissões do grupo {}: {}", groupId, e.getMessage(), e);
      throw new RuntimeException("Falha ao listar permissões do grupo", e);
    }
  }

  @Transactional
  public void assignPermission(Long groupId, String roleName) {
    try {
      AuthGroupEntity group = groupRepository.findByIdOptional(groupId)
          .orElseThrow(() -> new NotFoundException("Grupo não encontrado: " + groupId));

      List<String> currentPermissions = getGroupPermissions(groupId);
      if (currentPermissions.contains(roleName)) {
        log.warn("Role {} já existe no grupo {}", roleName, groupId);
        return;
      }

      if (roleName != null && !roleName.isBlank()) {
        group.getRoleNames().add(roleName);
      }
      groupRepository.persist(group);
      log.info("Role {} atribuída ao grupo {}", roleName, groupId);

    } catch (Exception e) {
      log.error("Erro ao atribuir role {} ao grupo {}: {}", roleName, groupId, e.getMessage(), e);
      throw new RuntimeException("Falha ao atribuir role ao grupo", e);
    }
  }

  @Transactional
  public void removePermission(Long groupId, String roleName) {
    try {
      AuthGroupEntity group = groupRepository.findByIdOptional(groupId)
          .orElseThrow(() -> new NotFoundException("Grupo não encontrado: " + groupId));

      List<String> currentPermissions = getGroupPermissions(groupId);
      if (!currentPermissions.contains(roleName)) {
        log.warn("Role {} não existe no grupo {}", roleName, groupId);
        return;
      }

      if (group.getRoleNames() != null) {
        group.getRoleNames().remove(roleName);
      }
      groupRepository.persist(group);
      log.info("Role {} removida do grupo {}", roleName, groupId);

    } catch (Exception e) {
      log.error("Erro ao remover role {} do grupo {}: {}", roleName, groupId, e.getMessage(), e);
      throw new RuntimeException("Falha ao remover role do grupo", e);
    }
  }


  public long countGroups() {
    try {
      List<GroupDTO> groups = findAll();
      return groups.size();
    } catch (Exception e) {
      log.error("Erro ao contar grupos: {}", e.getMessage(), e);
      return 0;
    }
  }

  public long countGroupMembers(Long groupId) {
    try {
      List<UserDTO> members = getGroupMembers(groupId);
      return members.size();
    } catch (Exception e) {
      log.error("Erro ao contar membros do grupo {}: {}", groupId, e.getMessage(), e);
      return 0;
    }
  }

  public boolean exists(Long groupId) {
    try {
      return groupRepository.findByIdOptional(groupId).isPresent();
    } catch (NotFoundException e) {
      return false;
    } catch (Exception e) {
      log.error("Erro ao verificar existência do grupo {}: {}", groupId, e.getMessage(), e);
      return false;
    }
  }

  public boolean existsByName(String name) {
    return findByName(name).isPresent();
  }

  private GroupDTO toDto(AuthGroupEntity group, boolean includeMembers) {
    List<String> roleNames = group.getRoleNames() == null ? List.of() : group.getRoleNames().stream().toList();

    List<String> members = includeMembers
        ? userRepository.find("select u from AuthUserEntity u join u.groups g where g.id = ?1", group.getId())
        .list()
        .stream()
        .map(AuthUserEntity::getId)
        .map(String::valueOf)
        .toList()
        : null;

    Long organizationId = group.getOrganization() != null ? group.getOrganization().getId() : null;
    return new GroupDTO(group.getId(), group.getName(), organizationId, group.getIsDefault(), roleNames, members);
  }

  private UserDTO toUserDto(AuthUserEntity user) {
    List<String> groupIds = user.getGroups() == null ? List.of() : user.getGroups().stream().map(AuthGroupEntity::getId).map(String::valueOf).toList();
    java.util.Set<String> roleNames = new java.util.HashSet<>();
    if (user.getGroups() != null) {
      user.getGroups().forEach(g -> {
        if (g != null && g.getRoleNames() != null) {
          g.getRoleNames().stream()
              .filter(r -> r != null && !r.isBlank())
              .forEach(roleNames::add);
        }
      });
    }
    return new UserDTO(
        user.getId(),
        user.getEmail(),
        user.getPhone(),
        user.getFirstName(),
        user.getLastName(),
        user.getBirthDate(),
        user.getTaxIdentifier(),
        user.getAddress(),
        user.getEnabled(),
        user.getStatus(),
        groupIds,
        roleNames.stream().toList()
    );
  }
}

