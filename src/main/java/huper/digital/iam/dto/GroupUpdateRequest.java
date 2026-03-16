package huper.digital.iam.dto;

import jakarta.validation.constraints.Size;

public record GroupUpdateRequest(
    @Size(max = 100, message = "Nome do grupo deve ter no máximo 100 caracteres")
    String name
) {}

