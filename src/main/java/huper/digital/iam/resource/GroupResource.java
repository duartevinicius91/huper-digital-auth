package huper.digital.iam.resource;

import huper.digital.iam.dto.GroupDTO;
import huper.digital.iam.resource.openapi.GroupResourceOpenApiDoc;
import huper.digital.iam.service.GroupService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

import static huper.digital.iam.constants.RoleConstants.*;

@Path("/groups")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GroupResource implements GroupResourceOpenApiDoc {

  @Inject
  GroupService groupService;

  @Override
  @GET
  @RolesAllowed({GROUP_MANAGEMENT_READ})
  public List<GroupDTO> getAll(
      @QueryParam("first") @DefaultValue("0") Integer first,
      @QueryParam("max") @DefaultValue("100") Integer max,
      @QueryParam("search") String search,
      @QueryParam("organizationId") Long organizationId) {

    return groupService.findAll(first, max, search, organizationId);
  }

  @Override
  @GET
  @Path("/{id}")
  @RolesAllowed({GROUP_MANAGEMENT_READ})
  public GroupDTO get(@PathParam("id") Long id) {
    return groupService.findById(id);
  }

  @Override
  @POST
  @RolesAllowed({GROUP_MANAGEMENT_WRITE})
  public Response create(GroupDTO groupDTO) {
    GroupDTO createdGroup = groupService.create(groupDTO);
    URI location = URI.create("/groups/" + createdGroup.id());
    return Response.created(location)
        .entity(createdGroup)
        .build();
  }

  @Override
  @PUT
  @Path("/{id}")
  @RolesAllowed({GROUP_MANAGEMENT_WRITE})
  public GroupDTO update(@PathParam("id") Long id, GroupDTO groupDTO) {
    return groupService.update(id, groupDTO);
  }

  @Override
  @DELETE
  @Path("/{id}")
  @RolesAllowed({GROUP_MANAGEMENT_DELETE})
  public Response delete(@PathParam("id") Long id) {
    groupService.delete(id);
    return Response.noContent().build();
  }

  @Override
  @POST
  @Path("/{id}/members/{userId}")
  @RolesAllowed({GROUP_MANAGEMENT_WRITE, USER_MANAGEMENT_WRITE})
  public Response addMember(
      @PathParam("id") Long groupId,
      @PathParam("userId") Long userId) {

    groupService.addMember(groupId, userId);
    return Response.noContent().build();
  }

  @Override
  @DELETE
  @Path("/{id}/members/{userId}")
  @RolesAllowed({GROUP_MANAGEMENT_WRITE, USER_MANAGEMENT_WRITE})
  public Response removeMember(
      @PathParam("id") Long groupId,
      @PathParam("userId") Long userId) {

    groupService.removeMember(groupId, userId);
    return Response.noContent().build();
  }

  @Override
  @POST
  @Path("/{id}/permissions")
  @RolesAllowed({GROUP_MANAGEMENT_WRITE})
  public Response addPermission(
      @PathParam("id") Long groupId,
      @QueryParam("permission") String permissionValue) {

    groupService.assignPermission(groupId, permissionValue);
    return Response.noContent().build();
  }

  @Override
  @DELETE
  @Path("/{id}/permissions")
  @RolesAllowed({GROUP_MANAGEMENT_WRITE})
  public Response removePermission(
      @PathParam("id") Long groupId,
      @QueryParam("permission") String permissionValue) {

    groupService.removePermission(groupId, permissionValue);
    return Response.noContent().build();
  }
}
