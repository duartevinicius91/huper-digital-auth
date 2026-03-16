package huper.digital.iam.resource.openapi;

import huper.digital.iam.dto.UserCreateRequest;
import huper.digital.iam.dto.UserDTO;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

@Tag(name = "User Management", description = "User management endpoints")
public interface UserResourceOpenApiDoc {

  @Operation(summary = "List users",
      description = "Lists all users with pagination and optional search")
  @APIResponses(value = {
      @APIResponse(responseCode = "200", description = "Users retrieved successfully",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = List.class))),
      @APIResponse(responseCode = "500", description = "Internal server error",
          content = @Content(mediaType = "application/json"))
  })
  Response getAll(
      @Parameter(description = "First result index", example = "0")
      Integer first,
      @Parameter(description = "Maximum number of results", example = "10")
      Integer max,
      @Parameter(description = "Search term")
      String search);

  @Operation(summary = "Get user by ID",
      description = "Retrieves a specific user by its internal ID")
  @APIResponses(value = {
      @APIResponse(responseCode = "200", description = "User retrieved successfully",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = Map.class))),
      @APIResponse(responseCode = "404", description = "User not found",
          content = @Content(mediaType = "application/json")),
      @APIResponse(responseCode = "500", description = "Internal server error",
          content = @Content(mediaType = "application/json"))
  })
  Response get(
      @Parameter(description = "User ID", example = "uuid-string", required = true)
      String userId);

  @Operation(summary = "Update user",
      description = "Updates an existing user's information")
  @APIResponses(value = {
      @APIResponse(responseCode = "200", description = "User updated successfully",
          content = @Content(mediaType = "application/json")),
      @APIResponse(responseCode = "400", description = "Update failed - invalid data",
          content = @Content(mediaType = "application/json")),
      @APIResponse(responseCode = "404", description = "User not found",
          content = @Content(mediaType = "application/json"))
  })
  Response update(
      @Parameter(description = "User ID", example = "uuid-string", required = true)
      String userId,
      @Parameter(description = "User update data", required = true)
      @Valid UserDTO request);

  @Operation(summary = "Delete user",
      description = "Deletes a user")
  @APIResponses(value = {
      @APIResponse(responseCode = "200", description = "User deleted successfully",
          content = @Content(mediaType = "application/json")),
      @APIResponse(responseCode = "400", description = "Deletion failed",
          content = @Content(mediaType = "application/json")),
      @APIResponse(responseCode = "404", description = "User not found",
          content = @Content(mediaType = "application/json"))
  })
  Response delete(
      @Parameter(description = "User ID", example = "uuid-string", required = true)
      String userId);


  @Operation(summary = "Create user",
      description = "Creates a new user")
  @APIResponses(value = {
      @APIResponse(responseCode = "201", description = "User created successfully",
          content = @Content(mediaType = "application/json")),
      @APIResponse(responseCode = "400", description = "User creation failed - invalid data",
          content = @Content(mediaType = "application/json"))
  })
  Response create(
      @Parameter(description = "User registration data", required = true)
      @Valid UserCreateRequest request);

}

