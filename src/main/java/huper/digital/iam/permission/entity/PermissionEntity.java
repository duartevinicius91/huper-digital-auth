package huper.digital.iam.permission.entity;

import jakarta.persistence.*;
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
public class PermissionEntity {

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
  private String permissionConstant;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  private PermissionEntity parent;

  @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
  private List<PermissionEntity> children = new ArrayList<>();

  @Column(name = "sort_order", nullable = false)
  private Integer sortOrder = 0;
}
