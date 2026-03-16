package huper.digital.iam.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserCreateRequest(
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Size(max = 255, message = "Email deve ter no máximo 255 caracteres")
    String email,

    @Size(max = 30, message = "Telefone deve ter no máximo 30 caracteres")
    String phone,

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    String password,

    String firstName,
    String lastName,
    
    @Past(message = "Data de nascimento deve ser uma data passada")
    LocalDate birthDate,
    
    @Size(max = 9, message = "Identificador fiscal deve ter no máximo 9 caracteres")
    String taxIdentifier,
    
    @Size(max = 200, message = "Endereço deve ter no máximo 200 caracteres")
    String address
) {
}

