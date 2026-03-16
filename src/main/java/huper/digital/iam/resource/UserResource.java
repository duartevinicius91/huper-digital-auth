package huper.digital.iam.resource;

import huper.digital.iam.dto.UserCreateRequest;
import huper.digital.iam.dto.UserDTO;
import huper.digital.iam.dto.UserInviteRequest;
import huper.digital.iam.resource.openapi.UserResourceOpenApiDoc;
import huper.digital.iam.service.UserService;
import huper.digital.common.dto.ResponseDto.ErrorResponse;
import huper.digital.common.dto.ResponseDto.SuccessResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import static huper.digital.iam.constants.RoleConstants.DASHBOARD_READ;
import static huper.digital.iam.constants.RoleConstants.USER_MANAGEMENT_READ;
import static huper.digital.iam.constants.RoleConstants.USER_MANAGEMENT_WRITE;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class UserResource implements UserResourceOpenApiDoc {

  @Inject
  UserService userService;

  @GET
  @RolesAllowed({USER_MANAGEMENT_READ, DASHBOARD_READ})
  public Response getAll(
      @QueryParam("first") @DefaultValue("0") Integer first,
      @QueryParam("max") @DefaultValue("100") Integer max,
      @QueryParam("search") String search) {
    try {
      log.info("Listando usuários - first: {}, max: {}, search: {}", first, max, search);
      var users = userService.findAll(first, max, search);
      log.info("Retornando {} usuários", users.size());
      return Response.ok(users).build();
    } catch (Exception e) {
      log.error("Erro ao listar usuários: {}", e.getMessage(), e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(new ErrorResponse("Erro interno", "Erro ao listar usuários: " + e.getMessage()))
          .build();
    }
  }

  @GET
  @Path("/{userId}")
  @RolesAllowed({USER_MANAGEMENT_READ})
  public Response get(@PathParam("userId") String userId) {
    try {
      var user = userService.findById(userId);
      return Response.ok(user).build();
    } catch (Exception e) {
      log.error("Erro ao buscar usuário: {}", e.getMessage(), e);
      return Response.status(Response.Status.NOT_FOUND)
          .entity(new ErrorResponse("Usuário não encontrado", "Usuário com ID " + userId + " não foi encontrado"))
          .build();
    }
  }

  @PUT
  @Path("/{userId}")
  @RolesAllowed({USER_MANAGEMENT_WRITE})
  public Response update(@PathParam("userId") String userId,
                         @Valid UserDTO request) {
    try {
      userService.update(userId, request);

      return Response.ok(new SuccessResponse("Usuário atualizado com sucesso")).build();
    } catch (Exception e) {
      log.error("Erro ao atualizar usuário: {}", e.getMessage(), e);
      return Response.status(Response.Status.BAD_REQUEST)
          .entity(new ErrorResponse("Erro na atualização", e.getMessage()))
          .build();
    }
  }

  @DELETE
  @Path("/{userId}")
  @RolesAllowed({USER_MANAGEMENT_WRITE})
  public Response delete(@PathParam("userId") String userId) {
    try {
      userService.delete(userId);

      return Response.ok(new SuccessResponse("Usuário removido com sucesso")).build();
    } catch (Exception e) {
      log.error("Erro ao remover usuário: {}", e.getMessage(), e);
      return Response.status(Response.Status.BAD_REQUEST)
          .entity(new ErrorResponse("Erro na remoção", e.getMessage()))
          .build();
    }
  }

  @POST
  @RolesAllowed({USER_MANAGEMENT_WRITE})
  public Response create(@Valid UserCreateRequest request) {
    try {
      userService.create(request);

      return Response.status(Response.Status.CREATED)
          .entity(new SuccessResponse("Usuário criado com sucesso"))
          .build();
    } catch (Exception e) {
      log.error("Erro ao criar usuário: {}", e.getMessage(), e);
      return Response.status(Response.Status.BAD_REQUEST)
          .entity(new ErrorResponse("Erro na criação", e.getMessage()))
          .build();
    }
  }

  @POST
  @Path("/invite")
  @RolesAllowed({USER_MANAGEMENT_WRITE})
  public Response invite(@Valid UserInviteRequest request) {
    try {
      userService.inviteUser(request);

      return Response.status(Response.Status.CREATED)
          .entity(new SuccessResponse("Convite enviado com sucesso"))
          .build();
    } catch (Exception e) {
      log.error("Erro ao enviar convite: {}", e.getMessage(), e);
      return Response.status(Response.Status.BAD_REQUEST)
          .entity(new ErrorResponse("Erro ao enviar convite", e.getMessage()))
          .build();
    }
  }

}
