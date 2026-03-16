package huper.digital.iam.resource;

import huper.digital.iam.dto.ChangePasswordRequest;
import huper.digital.iam.dto.LoginRequest;
import huper.digital.iam.dto.RefreshTokenRequest;
import huper.digital.iam.dto.UserCreateRequest;
import huper.digital.iam.service.AuthService;
import huper.digital.iam.service.UserService;
import org.eclipse.microprofile.jwt.JsonWebToken;
import huper.digital.common.dto.ResponseDto.ErrorResponse;
import huper.digital.common.dto.ResponseDto.SuccessResponse;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
@RequiredArgsConstructor
public class AuthResource {

  private final AuthService authService;
  private final UserService userService;

  @Inject
  JsonWebToken jwt;

  @POST
  @Path("/login")
  @PermitAll
  public Response login(@Valid LoginRequest request) {
    try {
      var response = authService.authenticate(request);

      return Response.ok(response).build();

    } catch (Exception e) {
      log.error("Erro durante login: {}", e.getMessage(), e);
      throw e;
    }
  }

  @POST
  @Path("/refresh")
  @PermitAll
  public Response refreshToken(@Valid RefreshTokenRequest request) {
    try {
      var response = authService.refreshToken(request);

      return Response.ok(response).build();

    } catch (Exception e) {
      log.error("Erro durante renovação de token: {}", e.getMessage(), e);
      throw e;
    }
  }

  @POST
  @Path("/register")
  @PermitAll
  public Response register(@Valid UserCreateRequest request) {
    try {
      userService.create(request);

      return Response.status(Response.Status.CREATED)
          .entity(new SuccessResponse("Usuário criado com sucesso"))
          .build();

    } catch (Exception e) {
      log.error("Erro durante registro: {}", e.getMessage(), e);
      throw e;
    }
  }

  @PUT
  @Path("/user/password")
  public Response changePassword(@Valid ChangePasswordRequest request) {
    try {
      // Get user ID from JWT token
      String userId = jwt.getClaim("userId");
      
      if (userId == null) {
        // Fallback to subject if userId claim is not available
        userId = jwt.getSubject();
      }
      
      if (userId == null) {
        return Response.status(Response.Status.UNAUTHORIZED)
            .entity(new ErrorResponse("Não autenticado", "Usuário não autenticado"))
            .build();
      }

      userService.changePassword(userId, request);

      return Response.ok(new SuccessResponse("Senha alterada com sucesso")).build();

    } catch (Exception e) {
      log.error("Erro ao alterar senha: {}", e.getMessage(), e);
      throw e;
    }
  }
}
