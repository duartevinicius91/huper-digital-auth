package huper.digital.iam.user.dto;

import java.time.LocalDate;
import java.util.List;

public record UserDTO(
    Long id,
    String email,
    String phone,
    String firstName,
    String lastName,
    LocalDate birthDate,
    String taxIdentifier,
    String address,
    Boolean enabled,
    String status,
    List<String> groups,
    List<String> realmRoles
) {}
