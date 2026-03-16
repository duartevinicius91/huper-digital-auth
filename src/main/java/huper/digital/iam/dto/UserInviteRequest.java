package huper.digital.iam.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserInviteRequest(
    @Size(max = 255, message = "Email deve ter no máximo 255 caracteres")
    @Email(message = "Email deve ser válido")
    String email,

    @Size(max = 30, message = "Telefone deve ter no máximo 30 caracteres")
    String phone,

    @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    String firstName,

    @Size(max = 255, message = "Sobrenome deve ter no máximo 255 caracteres")
    String lastName,

    String organizationId,

    java.util.List<String> groupIds
) {}


