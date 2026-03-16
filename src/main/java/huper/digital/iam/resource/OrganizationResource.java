package huper.digital.iam.resource;

import huper.digital.iam.dto.OrganizationDTO;
import huper.digital.iam.dto.OrganizationUpsertRequest;
import huper.digital.iam.service.OrganizationService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

import static huper.digital.iam.constants.RoleConstants.USER_MANAGEMENT_READ;
import static huper.digital.iam.constants.RoleConstants.USER_MANAGEMENT_WRITE;

@Path("/organizations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrganizationResource {

  @Inject
  OrganizationService organizationService;

  @GET
  @RolesAllowed({USER_MANAGEMENT_READ})
  public List<OrganizationDTO> list() {
    return organizationService.findAll();
  }

  @GET
  @Path("/{id}")
  @RolesAllowed({USER_MANAGEMENT_READ})
  public OrganizationDTO get(@PathParam("id") Long id) {
    return organizationService.findById(id);
  }

  @POST
  @RolesAllowed({USER_MANAGEMENT_WRITE})
  public Response create(@Valid OrganizationUpsertRequest request) {
    OrganizationDTO created = organizationService.create(request);
    return Response.created(URI.create("/organizations/" + created.id())).entity(created).build();
  }

  @PUT
  @Path("/{id}")
  @RolesAllowed({USER_MANAGEMENT_WRITE})
  public OrganizationDTO update(@PathParam("id") Long id, @Valid OrganizationUpsertRequest request) {
    return organizationService.update(id, request);
  }

  @DELETE
  @Path("/{id}")
  @RolesAllowed({USER_MANAGEMENT_WRITE})
  public Response delete(@PathParam("id") Long id) {
    organizationService.delete(id);
    return Response.noContent().build();
  }
}

