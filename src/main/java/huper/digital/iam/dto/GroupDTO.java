package huper.digital.iam.dto;

import java.util.List;

public record GroupDTO(
    Long id,
    String name,
    Long organizationId,
    Boolean isDefault,
    List<String> realmRoles,
    List<String> members
) {
}

