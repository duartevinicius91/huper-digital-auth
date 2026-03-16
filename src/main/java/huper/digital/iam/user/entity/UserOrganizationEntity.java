package huper.digital.iam.user.entity;

import huper.digital.iam.tenant.entity.TenantEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "auth_user_organizations")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserOrganizationEntity {

  @EmbeddedId
  @EqualsAndHashCode.Include
  private UserOrganizationId id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId("userId")
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId("organizationId")
  @JoinColumn(name = "organization_id", nullable = false)
  private TenantEntity organization;

  @Column(name = "is_owner", nullable = false)
  private Boolean owner = false;
}
