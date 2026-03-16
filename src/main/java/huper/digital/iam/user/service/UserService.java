package huper.digital.iam.user.service;

import huper.digital.iam.common.exception.AuthenticationException;
import huper.digital.iam.common.exception.UserAlreadyExistsException;
import huper.digital.iam.groups.dto.GroupDTO;
import huper.digital.iam.groups.entity.GroupEntity;
import huper.digital.iam.groups.service.GroupService;
import huper.digital.iam.permission.service.PermissionService;
import huper.digital.iam.security.JwtTokenService;
import huper.digital.iam.tenant.entity.TenantEntity;
import huper.digital.iam.user.dto.*;
import huper.digital.iam.user.entity.UserEntity;
import huper.digital.iam.user.entity.UserOrganizationEntity;
import huper.digital.iam.user.entity.UserOrganizationId;
import huper.digital.iam.user.repository.UserRepository;
import huper.digital.iam.groups.repository.GroupRepository;
import huper.digital.iam.tenant.repository.TenantRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
public class UserService {

  @Inject
  UserRepository userRepository;
  @Inject
  GroupRepository groupRepository;
  @Inject
  TenantRepository tenantRepository;
  @Inject
  GroupService groupService;
  @Inject
  JwtTokenService jwtTokenService;
  @Inject
  PermissionService permissionService;

  public List<UserDTO> findAll(Integer first, Integer max, String search) {
    int safeFirst = first == null ? 0 : Math.max(0, first);
    int safeMax = max == null ? 100 : Math.max(1, max);
    List<UserEntity> entities = StringUtils.isBlank(search)
        ? userRepository.findAll().list()
        : userRepository.find(
            "LOWER(email) LIKE ?1 OR LOWER(firstName) LIKE ?1 OR LOWER(lastName) LIKE ?1",
            "%" + search.toLowerCase() + "%").list();
    int end = Math.min(safeFirst + safeMax, entities.size());
    List<UserEntity> page = safeFirst >= entities.size() ? List.of() : entities.subList(safeFirst, end);
    return page.stream().map(this::toDto).toList();
  }

  public List<UserDTO> findAll() {
    return findAll(0, 1000, null);
  }

  public UserDTO findById(String userId) {
    Long id = Long.parseLong(userId);
    return userRepository.findByIdOptional(id)
        .map(this::toDto)
        .orElseThrow(() -> new NotFoundException("Usuário não encontrado: " + userId));
  }

  public Optional<UserDTO> findByEmail(String email) {
    return userRepository.findByEmail(email).map(this::toDto);
  }

  @Transactional
  public UserDTO create(UserCreateRequest request) {
    if (userRepository.findByEmail(request.email()).isPresent()) {
      throw new UserAlreadyExistsException("Já existe um usuário com o email: " + request.email());
    }
    UserEntity entity = new UserEntity();
    entity.setEmail(request.email());
    entity.setPhone(request.phone());
    entity.setFirstName(request.firstName());
    entity.setLastName(request.lastName());
    entity.setBirthDate(request.birthDate());
    entity.setTaxIdentifier(request.taxIdentifier());
    entity.setAddress(request.address());
    entity.setPasswordHash(jwtTokenService.hashPassword(request.password()));
    entity.setEnabled(true);
    entity.setStatus("ATIVO");
    userRepository.persist(entity);

    String orgName = StringUtils.isNotBlank(request.firstName()) ? request.firstName() : request.email().split("@")[0];
    TenantEntity organization = new TenantEntity();
    organization.setName(orgName);
    organization.setIsActive(true);
    tenantRepository.persist(organization);

    UserOrganizationEntity uo = new UserOrganizationEntity();
    uo.setId(new UserOrganizationId(entity.getId(), organization.getId()));
    uo.setUser(entity);
    uo.setOrganization(organization);
    uo.setOwner(true);
    entity.getOrganizationMemberships().add(uo);
    userRepository.persist(entity);

    Optional<GroupDTO> existingGroup = groupService.findByName("Administradores");
    GroupEntity adminGroup;
    if (existingGroup.isPresent()) {
      adminGroup = groupRepository.findByIdOptional(existingGroup.get().id())
          .orElseThrow(() -> new RuntimeException("Grupo Administradores não encontrado"));
    } else {
      adminGroup = new GroupEntity();
      adminGroup.setName("Administradores");
      adminGroup.setOrganization(organization);
      adminGroup.getRoleNames().addAll(permissionService.getAllPermissions());
      groupRepository.persist(adminGroup);
    }
    entity.getGroups().add(adminGroup);
    userRepository.persist(entity);
    return toDto(entity);
  }

  @Transactional
  public UserDTO update(String userId, UserDTO userDTO) {
    Long id = Long.parseLong(userId);
    UserEntity entity = userRepository.findByIdOptional(id)
        .orElseThrow(() -> new NotFoundException("Usuário não encontrado: " + userId));
    if (StringUtils.isNotBlank(userDTO.email()) && !entity.getEmail().equalsIgnoreCase(userDTO.email())) {
      userRepository.findByEmail(userDTO.email()).ifPresent(u -> {
        if (!u.getId().equals(id)) throw new UserAlreadyExistsException("Já existe outro usuário com o email: " + userDTO.email());
      });
      entity.setEmail(userDTO.email());
    }
    entity.setPhone(userDTO.phone());
    entity.setFirstName(userDTO.firstName());
    entity.setLastName(userDTO.lastName());
    entity.setTaxIdentifier(userDTO.taxIdentifier());
    entity.setAddress(userDTO.address());
    if (userDTO.enabled() != null) entity.setEnabled(userDTO.enabled());
    userRepository.persist(entity);
    return toDto(entity);
  }

  @Transactional
  public void delete(String userId) {
    Long id = Long.parseLong(userId);
    UserEntity entity = userRepository.findByIdOptional(id)
        .orElseThrow(() -> new NotFoundException("Usuário não encontrado: " + userId));
    userRepository.delete(entity);
  }

  public List<GroupDTO> getUserGroups(String userId) {
    Long id = Long.parseLong(userId);
    UserEntity user = userRepository.findByIdOptional(id)
        .orElseThrow(() -> new NotFoundException("Usuário não encontrado: " + userId));
    return user.getGroups().stream().map(g -> new GroupDTO(
        g.getId(), g.getName(),
        g.getOrganization() != null ? g.getOrganization().getId() : null,
        g.getIsDefault(),
        g.getRoleNames() != null ? new ArrayList<>(g.getRoleNames()) : List.of(),
        g.getPermissions() != null ? g.getPermissions().stream().map(p -> p.getId()).collect(Collectors.toList()) : List.of(),
        null)).toList();
  }

  @Transactional
  public void addToGroup(Long userId, Long groupId) {
    UserEntity user = userRepository.findByIdOptional(userId)
        .orElseThrow(() -> new NotFoundException("Usuário não encontrado: " + userId));
    GroupEntity group = groupRepository.findByIdOptional(groupId)
        .orElseThrow(() -> new NotFoundException("Grupo não encontrado: " + groupId));
    user.getGroups().add(group);
    userRepository.persist(user);
  }

  @Transactional
  public void removeFromGroup(Long userId, Long groupId) {
    UserEntity user = userRepository.findByIdOptional(userId)
        .orElseThrow(() -> new NotFoundException("Usuário não encontrado: " + userId));
    GroupEntity group = groupRepository.findByIdOptional(groupId)
        .orElseThrow(() -> new NotFoundException("Grupo não encontrado: " + groupId));
    user.getGroups().remove(group);
    userRepository.persist(user);
  }

  @Transactional
  public void changePassword(String userId, huper.digital.iam.auth.dto.ChangePasswordRequest request) {
    Long id = Long.parseLong(userId);
    UserEntity user = userRepository.findByIdOptional(id)
        .orElseThrow(() -> new NotFoundException("Usuário não encontrado: " + userId));
    if (!jwtTokenService.matchesPassword(request.currentPassword(), user.getPasswordHash())) {
      throw new AuthenticationException("Senha atual incorreta");
    }
    user.setPasswordHash(jwtTokenService.hashPassword(request.newPassword()));
    userRepository.persist(user);
  }

  @Transactional
  public void inviteUser(UserInviteRequest request) {
    if (StringUtils.isBlank(request.email()) && StringUtils.isBlank(request.phone())) {
      throw new IllegalArgumentException("Email ou telefone deve ser fornecido");
    }
    if (StringUtils.isNotBlank(request.email()) && userRepository.findByEmail(request.email()).isPresent()) {
      throw new UserAlreadyExistsException("Já existe um usuário com o email: " + request.email());
    }
    String tempPassword = UUID.randomUUID().toString().substring(0, 12);
    UserEntity entity = new UserEntity();
    entity.setEmail(StringUtils.isNotBlank(request.email()) ? request.email() : "temp_" + request.phone().replaceAll("[^0-9]", "") + "@invite.temp");
    entity.setPhone(request.phone());
    entity.setFirstName(request.firstName());
    entity.setLastName(request.lastName());
    entity.setPasswordHash(jwtTokenService.hashPassword(tempPassword));
    entity.setEnabled(true);
    entity.setStatus("CONVIDADO");
    userRepository.persist(entity);

    if (StringUtils.isNotBlank(request.organizationId())) {
      Long orgId = Long.parseLong(request.organizationId());
      TenantEntity org = tenantRepository.findByIdOptional(orgId)
          .orElseThrow(() -> new NotFoundException("Organização não encontrada: " + request.organizationId()));
      UserOrganizationEntity uo = new UserOrganizationEntity();
      uo.setId(new UserOrganizationId(entity.getId(), org.getId()));
      uo.setUser(entity);
      uo.setOrganization(org);
      uo.setOwner(false);
      entity.getOrganizationMemberships().add(uo);
    }
    if (request.groupIds() != null) {
      for (String gid : request.groupIds()) {
        try {
          Long groupId = Long.parseLong(gid);
          groupRepository.findByIdOptional(groupId).ifPresent(g -> entity.getGroups().add(g));
        } catch (NumberFormatException ignored) {}
      }
    }
    userRepository.persist(entity);
  }

  private UserDTO toDto(UserEntity user) {
    List<String> groupIds = user.getGroups() == null ? List.of()
        : user.getGroups().stream().map(g -> String.valueOf(g.getId())).toList();
    Set<String> roleNames = new HashSet<>();
    if (user.getGroups() != null) {
      user.getGroups().forEach(g -> {
        if (g.getRoleNames() != null) g.getRoleNames().forEach(roleNames::add);
        if (g.getPermissions() != null) {
          g.getPermissions().stream().map(p -> p.getPermissionConstant()).filter(Objects::nonNull).forEach(roleNames::add);
        }
      });
    }
    return new UserDTO(user.getId(), user.getEmail(), user.getPhone(), user.getFirstName(), user.getLastName(),
        user.getBirthDate(), user.getTaxIdentifier(), user.getAddress(), user.getEnabled(), user.getStatus(),
        groupIds, new ArrayList<>(roleNames));
  }
}
