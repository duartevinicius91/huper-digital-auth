package huper.digital.iam.dto;

import java.util.List;

public record UserUpdateRequest(
        String name,
        String phone,
        String firstName,
        String lastName,
        Boolean enabled,
        List<String> roleIds
) {
}

