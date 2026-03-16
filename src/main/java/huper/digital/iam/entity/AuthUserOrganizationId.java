package huper.digital.iam.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class AuthUserOrganizationId implements Serializable {

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "organization_id", nullable = false)
  private Long organizationId;

  public AuthUserOrganizationId(Long userId, Long organizationId) {
    this.userId = userId;
    this.organizationId = organizationId;
  }
}

