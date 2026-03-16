package huper.digital.iam.tenant.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TenantUpsertRequest(
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 255)
    String name,
    @Size(max = 9)
    String taxIdentifier,
    @Size(max = 200)
    String address,
    @Email
    @Size(max = 255)
    String email,
    @Size(max = 30)
    String phone,
    LocalDate foundingDate,
    Boolean active
) {}
