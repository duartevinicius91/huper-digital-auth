package huper.digital.iam.auth.resource;

import huper.digital.iam.auth.dto.ChangePasswordRequest;
import huper.digital.iam.auth.dto.LoginRequest;
import huper.digital.iam.auth.dto.RefreshTokenRequest;
import huper.digital.iam.auth.service.AuthService;
import huper.digital.iam.common.dto.ResponseDto;
import huper.digital.iam.user.dto.UserCreateRequest;
import huper.digital.iam.user.service.UserService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class AuthResource {

  @Inject
  AuthService authService;
  @Inject
  UserService userService;
  @Inject
  JsonWebToken jwt;

  @POST
  @Path("/login")
  @PermitAll
  public Response login(@Valid LoginRequest request) {
    var response = authService.authenticate(request);
    return Response.ok(response).build();
  }

  @POST
  @Path("/refresh")
  @PermitAll
  public Response refreshToken(@Valid RefreshTokenRequest request) {
    var response = authService.refreshToken(request);
    return Response.ok(response).build();
  }

  @POST
  @Path("/register")
  @PermitAll
  public Response register(@Valid UserCreateRequest request) {
    userService.create(request);
    return Response.status(Response.Status.CREATED).entity(new ResponseDto.SuccessResponse("Usuário criado com sucesso")).build();
  }

  @PUT
  @Path("/user/password")
  public Response changePassword(@Valid ChangePasswordRequest request) {
    String userId = jwt.getClaim("userId");
    if (userId == null) userId = jwt.getSubject();
    if (userId == null) {
      return Response.status(Response.Status.UNAUTHORIZED).entity(new ResponseDto.ErrorResponse("Não autenticado", "Usuário não autenticado")).build();
    }
    userService.changePassword(userId, request);
    return Response.ok(new ResponseDto.SuccessResponse("Senha alterada com sucesso")).build();
  }
}
