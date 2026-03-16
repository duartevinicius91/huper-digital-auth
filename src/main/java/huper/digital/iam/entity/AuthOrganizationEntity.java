package huper.digital.iam.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "auth_organizations")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AuthOrganizationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  @EqualsAndHashCode.Include
  private Long id;

  @Column(name = "name", nullable = false, length = 255)
  private String name;

  @Column(name = "tax_identifier", length = 9)
  private String taxIdentifier;

  @Column(name = "address", length = 200)
  private String address;

  @Column(name = "email", length = 255)
  private String email;

  @Column(name = "phone", length = 30)
  private String phone;

  @Column(name = "founding_date")
  private LocalDate foundingDate;

  @Column(name = "is_active", nullable = false)
  private Boolean isActive = true;

  @Column(name = "is_default", nullable = false)
  private Boolean isDefault = false;

  @Column(name = "created_at", nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  private LocalDateTime updatedAt;
}

