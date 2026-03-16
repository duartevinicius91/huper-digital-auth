package huper.digital.iam.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "auth_permissions")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AuthPermissionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  @EqualsAndHashCode.Include
  private Long id;

  @Column(name = "name", nullable = false, length = 255)
  private String name;

  @Column(name = "description", length = 1000)
  private String description;

  @Column(name = "permission_constant", length = 100, unique = true)
  private String permissionConstant; // null for category nodes, non-null for leaf nodes

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  private AuthPermissionEntity parent;

  @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
  private List<AuthPermissionEntity> children = new ArrayList<>();

  @Column(name = "sort_order", nullable = false)
  private Integer sortOrder = 0;
}

