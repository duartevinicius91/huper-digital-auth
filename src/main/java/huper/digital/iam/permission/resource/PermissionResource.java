package huper.digital.iam.permission.resource;

import huper.digital.iam.permission.dto.PermissionNodeDTO;
import huper.digital.iam.permission.service.PermissionService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import static huper.digital.iam.common.constants.RoleConstants.GROUP_MANAGEMENT_READ;

@Path("/permissions")
@Produces(MediaType.APPLICATION_JSON)
public class PermissionResource {

  @Inject
  PermissionService permissionService;

  @GET
  @Path("/tree")
  @RolesAllowed({GROUP_MANAGEMENT_READ})
  public PermissionNodeDTO getPermissionTree() {
    return permissionService.getPermissionTreeDto();
  }
}
