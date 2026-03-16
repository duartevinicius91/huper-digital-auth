package huper.digital.iam.tenant.resource;

import huper.digital.iam.tenant.dto.TenantDTO;
import huper.digital.iam.tenant.dto.TenantUpsertRequest;
import huper.digital.iam.tenant.service.TenantService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

import static huper.digital.iam.common.constants.RoleConstants.USER_MANAGEMENT_READ;
import static huper.digital.iam.common.constants.RoleConstants.USER_MANAGEMENT_WRITE;

/**
 * Recurso em /organizations para compatibilidade. Delega para TenantService.
 * Novos clientes devem usar /tenants.
 */
@Path("/organizations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrganizationResource {

  @Inject
  TenantService tenantService;

  @GET
  @RolesAllowed({USER_MANAGEMENT_READ})
  public List<TenantDTO> list() {
    return tenantService.findAll();
  }

  @GET
  @Path("/{id}")
  @RolesAllowed({USER_MANAGEMENT_READ})
  public TenantDTO get(@PathParam("id") Long id) {
    return tenantService.findById(id);
  }

  @POST
  @RolesAllowed({USER_MANAGEMENT_WRITE})
  public Response create(@Valid TenantUpsertRequest request) {
    TenantDTO created = tenantService.create(request);
    return Response.created(URI.create("/organizations/" + created.id())).entity(created).build();
  }

  @PUT
  @Path("/{id}")
  @RolesAllowed({USER_MANAGEMENT_WRITE})
  public TenantDTO update(@PathParam("id") Long id, @Valid TenantUpsertRequest request) {
    return tenantService.update(id, request);
  }

  @DELETE
  @Path("/{id}")
  @RolesAllowed({USER_MANAGEMENT_WRITE})
  public Response delete(@PathParam("id") Long id) {
    tenantService.delete(id);
    return Response.noContent().build();
  }
}
