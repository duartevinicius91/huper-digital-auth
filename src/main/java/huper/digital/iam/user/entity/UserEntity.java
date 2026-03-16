package huper.digital.iam.user.entity;

import huper.digital.iam.groups.entity.GroupEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "auth_users")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  @EqualsAndHashCode.Include
  private Long id;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<UserOrganizationEntity> organizationMemberships = new HashSet<>();

  @Column(name = "email", nullable = false, length = 255)
  private String email;

  @Column(name = "phone", length = 30)
  private String phone;

  @Column(name = "password_hash", nullable = false, length = 100)
  private String passwordHash;

  @Column(name = "first_name", length = 255)
  private String firstName;

  @Column(name = "last_name", length = 255)
  private String lastName;

  @Column(name = "birth_date")
  private LocalDate birthDate;

  @Column(name = "tax_identifier", length = 9)
  private String taxIdentifier;

  @Column(name = "address", length = 200)
  private String address;

  @Column(name = "enabled", nullable = false)
  private Boolean enabled = true;

  @Column(name = "status", nullable = false, length = 20)
  private String status = "ATIVO";

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "auth_user_groups",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "group_id")
  )
  private Set<GroupEntity> groups = new HashSet<>();

  @Column(name = "created_at", nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  private LocalDateTime updatedAt;
}
