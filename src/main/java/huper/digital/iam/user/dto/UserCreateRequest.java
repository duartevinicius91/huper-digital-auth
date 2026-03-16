package huper.digital.iam.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserCreateRequest(
    @NotBlank @Email @Size(max = 255)
    String email,
    @Size(max = 30)
    String phone,
    @NotBlank @Size(min = 8)
    String password,
    String firstName,
    String lastName,
    @Past
    LocalDate birthDate,
    @Size(max = 9)
    String taxIdentifier,
    @Size(max = 200)
    String address
) {}
