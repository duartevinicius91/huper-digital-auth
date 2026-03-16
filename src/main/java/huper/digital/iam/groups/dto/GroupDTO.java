package huper.digital.iam.groups.dto;

import java.util.List;

public record GroupDTO(
    Long id,
    String name,
    Long organizationId,
    Boolean isDefault,
    List<String> realmRoles,
    List<Long> permissionIds,
    List<String> members
) {}
