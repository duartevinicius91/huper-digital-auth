package huper.digital.iam.auth.resource;

import huper.digital.iam.common.constants.RoleConstants;
import huper.digital.iam.permission.service.PermissionService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/roles")
@Produces(MediaType.APPLICATION_JSON)
public class RoleResource {

  @Inject
  PermissionService permissionService;

  @GET
  @Path("/descriptions")
  @RolesAllowed({RoleConstants.DASHBOARD_READ})
  public Response getRoleDescriptions() {
    return Response.ok(permissionService.getAllDescriptions()).build();
  }
}
