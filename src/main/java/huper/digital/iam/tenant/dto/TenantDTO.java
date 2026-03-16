package huper.digital.iam.tenant.dto;

import java.time.LocalDate;

public record TenantDTO(
    Long id,
    String name,
    String taxIdentifier,
    String address,
    String email,
    String phone,
    LocalDate foundingDate,
    Boolean active,
    Boolean isDefault
) {}
