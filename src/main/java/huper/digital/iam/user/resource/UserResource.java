package huper.digital.iam.user.resource;

import huper.digital.iam.common.dto.ResponseDto;
import huper.digital.iam.user.dto.*;
import huper.digital.iam.user.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import static huper.digital.iam.common.constants.RoleConstants.*;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class UserResource {

  @Inject
  UserService userService;

  @GET
  @RolesAllowed({USER_MANAGEMENT_READ, DASHBOARD_READ})
  public Response getAll(@QueryParam("first") @DefaultValue("0") Integer first,
                         @QueryParam("max") @DefaultValue("100") Integer max,
                         @QueryParam("search") String search) {
    return Response.ok(userService.findAll(first, max, search)).build();
  }

  @GET
  @Path("/{userId}")
  @RolesAllowed({USER_MANAGEMENT_READ})
  public Response get(@PathParam("userId") String userId) {
    return Response.ok(userService.findById(userId)).build();
  }

  @PUT
  @Path("/{userId}")
  @RolesAllowed({USER_MANAGEMENT_WRITE})
  public Response update(@PathParam("userId") String userId, @Valid UserDTO request) {
    return Response.ok(userService.update(userId, request)).build();
  }

  @DELETE
  @Path("/{userId}")
  @RolesAllowed({USER_MANAGEMENT_WRITE})
  public Response delete(@PathParam("userId") String userId) {
    userService.delete(userId);
    return Response.ok(new ResponseDto.SuccessResponse("Usuário removido com sucesso")).build();
  }

  @POST
  @RolesAllowed({USER_MANAGEMENT_WRITE})
  public Response create(@Valid UserCreateRequest request) {
    var created = userService.create(request);
    return Response.status(Response.Status.CREATED).entity(created).build();
  }

  @POST
  @Path("/invite")
  @RolesAllowed({USER_MANAGEMENT_WRITE})
  public Response invite(@Valid UserInviteRequest request) {
    userService.inviteUser(request);
    return Response.status(Response.Status.CREATED).entity(new ResponseDto.SuccessResponse("Convite enviado com sucesso")).build();
  }
}
