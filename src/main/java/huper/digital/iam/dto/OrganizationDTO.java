package huper.digital.iam.dto;

import java.time.LocalDate;

public record OrganizationDTO(
    Long id,
    String name,
    String taxIdentifier,
    String address,
    String email,
    String phone,
    LocalDate foundingDate,
    Boolean active,
    Boolean isDefault
) {
}

