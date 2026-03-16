package huper.digital.iam.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UserInviteRequest(
    @Size(max = 255) @Email
    String email,
    @Size(max = 30)
    String phone,
    @Size(max = 255)
    String firstName,
    @Size(max = 255)
    String lastName,
    String organizationId,
    List<String> groupIds
) {}
