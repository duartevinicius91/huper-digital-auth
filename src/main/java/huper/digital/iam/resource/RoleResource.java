package huper.digital.iam.resource;

import huper.digital.iam.constants.RoleConstants;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

import static huper.digital.iam.constants.RoleConstants.DASHBOARD_READ;

@Path("/roles")
@Produces(MediaType.APPLICATION_JSON)
public class RoleResource {

  @Inject
  RoleConstants roleConstants;

  @GET
  @Path("/descriptions")
  @RolesAllowed({DASHBOARD_READ})
  public Response getRoleDescriptions() {
    Map<String, String> descriptions = roleConstants.getAllDescriptions();
    return Response.ok(descriptions).build();
  }
}

