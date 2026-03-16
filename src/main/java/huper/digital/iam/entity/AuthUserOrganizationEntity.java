package huper.digital.iam.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "auth_user_organizations")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AuthUserOrganizationEntity {

  @EmbeddedId
  @EqualsAndHashCode.Include
  private AuthUserOrganizationId id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId("userId")
  @JoinColumn(name = "user_id", nullable = false)
  private AuthUserEntity user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId("organizationId")
  @JoinColumn(name = "organization_id", nullable = false)
  private AuthOrganizationEntity organization;

  @Column(name = "is_owner", nullable = false)
  private Boolean owner = false;
}

