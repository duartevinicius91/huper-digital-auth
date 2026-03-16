package huper.digital.iam.permission.dto;

import java.util.List;

public record PermissionNodeDTO(
    Long id,
    String name,
    String description,
    String permissionConstant,
    List<PermissionNodeDTO> children,
    Integer sortOrder
) {}
