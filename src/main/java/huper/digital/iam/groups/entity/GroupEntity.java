package huper.digital.iam.groups.entity;

import huper.digital.iam.permission.entity.PermissionEntity;
import huper.digital.iam.tenant.entity.TenantEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "auth_groups")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class GroupEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  @EqualsAndHashCode.Include
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "organization_id")
  private TenantEntity organization;

  @Column(name = "name", nullable = false, length = 255)
  private String name;

  @Column(name = "is_default", nullable = false)
  private Boolean isDefault = false;

  /** @deprecated Prefer {@link #permissions}. Kept for backward compatibility with auth_group_roles. */
  @Deprecated
  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(name = "auth_group_roles", joinColumns = @JoinColumn(name = "group_id"))
  @Column(name = "role_name", nullable = false, length = 100)
  private Set<String> roleNames = new HashSet<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "auth_group_permissions",
      joinColumns = @JoinColumn(name = "group_id"),
      inverseJoinColumns = @JoinColumn(name = "permission_id")
  )
  private Set<PermissionEntity> permissions = new HashSet<>();

  @Column(name = "created_at", nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  private LocalDateTime updatedAt;
}
