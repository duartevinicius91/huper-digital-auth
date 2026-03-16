package huper.digital.iam.groups.resource;

import huper.digital.iam.groups.dto.GroupDTO;
import huper.digital.iam.groups.service.GroupService;
import huper.digital.iam.user.dto.UserDTO;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

import static huper.digital.iam.common.constants.RoleConstants.*;

@Path("/groups")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GroupResource {

  @Inject
  GroupService groupService;

  @GET
  @RolesAllowed({GROUP_MANAGEMENT_READ})
  public List<GroupDTO> getAll(@QueryParam("first") @DefaultValue("0") Integer first,
                               @QueryParam("max") @DefaultValue("100") Integer max,
                               @QueryParam("search") String search,
                               @QueryParam("organizationId") Long organizationId) {
    return groupService.findAll(first, max, search, organizationId);
  }

  @GET
  @Path("/{id}")
  @RolesAllowed({GROUP_MANAGEMENT_READ})
  public GroupDTO get(@PathParam("id") Long id) {
    return groupService.findById(id);
  }

  @POST
  @RolesAllowed({GROUP_MANAGEMENT_WRITE})
  public Response create(GroupDTO groupDTO) {
    GroupDTO created = groupService.create(groupDTO);
    return Response.created(URI.create("/groups/" + created.id())).entity(created).build();
  }

  @PUT
  @Path("/{id}")
  @RolesAllowed({GROUP_MANAGEMENT_WRITE})
  public GroupDTO update(@PathParam("id") Long id, GroupDTO groupDTO) {
    return groupService.update(id, groupDTO);
  }

  @DELETE
  @Path("/{id}")
  @RolesAllowed({GROUP_MANAGEMENT_DELETE})
  public Response delete(@PathParam("id") Long id) {
    groupService.delete(id);
    return Response.noContent().build();
  }

  @GET
  @Path("/{id}/members")
  @RolesAllowed({GROUP_MANAGEMENT_READ, USER_MANAGEMENT_READ})
  public List<UserDTO> getMembers(@PathParam("id") Long groupId,
                                  @QueryParam("first") @DefaultValue("0") Integer first,
                                  @QueryParam("max") @DefaultValue("100") Integer max) {
    return groupService.getGroupMembers(groupId, first, max);
  }

  @POST
  @Path("/{id}/members/{userId}")
  @RolesAllowed({GROUP_MANAGEMENT_WRITE, USER_MANAGEMENT_WRITE})
  public Response addMember(@PathParam("id") Long groupId, @PathParam("userId") Long userId) {
    groupService.addMember(groupId, userId);
    return Response.noContent().build();
  }

  @DELETE
  @Path("/{id}/members/{userId}")
  @RolesAllowed({GROUP_MANAGEMENT_WRITE, USER_MANAGEMENT_WRITE})
  public Response removeMember(@PathParam("id") Long groupId, @PathParam("userId") Long userId) {
    groupService.removeMember(groupId, userId);
    return Response.noContent().build();
  }

  @GET
  @Path("/{id}/permissions")
  @RolesAllowed({GROUP_MANAGEMENT_READ})
  public List<String> getPermissions(@PathParam("id") Long groupId) {
    return groupService.getGroupPermissionConstants(groupId);
  }

  @POST
  @Path("/{id}/permissions/{permissionId}")
  @RolesAllowed({GROUP_MANAGEMENT_WRITE})
  public Response assignPermission(@PathParam("id") Long groupId, @PathParam("permissionId") Long permissionId) {
    groupService.assignPermission(groupId, permissionId);
    return Response.noContent().build();
  }

  @DELETE
  @Path("/{id}/permissions/{permissionId}")
  @RolesAllowed({GROUP_MANAGEMENT_WRITE})
  public Response removePermission(@PathParam("id") Long groupId, @PathParam("permissionId") Long permissionId) {
    groupService.removePermission(groupId, permissionId);
    return Response.noContent().build();
  }
}
