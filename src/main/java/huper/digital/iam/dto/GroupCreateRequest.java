package huper.digital.iam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record GroupCreateRequest(
    @NotBlank(message = "Nome do grupo é obrigatório")
    @Size(max = 100, message = "Nome do grupo deve ter no máximo 100 caracteres")
    String name,
    
    List<String> realmRoles
) {}

