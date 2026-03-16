package huper.digital.iam.auth.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public record TokenResponse(
    @JsonAlias("access_token")
    String accessToken,
    @JsonAlias("refresh_token")
    String refreshToken,
    @JsonAlias("expires_in")
    Long expiresIn
) {}
