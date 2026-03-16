package huper.digital.iam.resource.openapi;

import huper.digital.iam.dto.GroupDTO;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

/**
 * Interface OpenAPI para documentação do GroupResource.
 * Define a documentação da API REST para gestão de grupos.
 */
@Tag(name = "Groups", description = "Operações de gestão de grupos e permissões")
public interface GroupResourceOpenApiDoc {

  @Operation(
      summary = "Listar grupos",
      description = "Lista todos os grupos com paginação e busca opcional. Pode filtrar por organização."
  )
  @APIResponses({
      @APIResponse(
          responseCode = "200",
          description = "Lista de grupos retornada com sucesso",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(type = SchemaType.ARRAY, implementation = GroupDTO.class)
          )
      ),
      @APIResponse(responseCode = "401", description = "Não autorizado"),
      @APIResponse(responseCode = "403", description = "Sem permissão")
  })
  List<GroupDTO> getAll(
      @QueryParam("first") Integer first,
      @QueryParam("max") Integer max,
      @QueryParam("search") String search,
      @QueryParam("organizationId") Long organizationId
  );

  @Operation(
      summary = "Buscar grupo por ID",
      description = "Retorna um grupo específico pelo ID"
  )
  @APIResponses({
      @APIResponse(
          responseCode = "200",
          description = "Grupo encontrado",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = GroupDTO.class)
          )
      ),
      @APIResponse(responseCode = "404", description = "Grupo não encontrado"),
      @APIResponse(responseCode = "401", description = "Não autorizado"),
      @APIResponse(responseCode = "403", description = "Sem permissão")
  })
  GroupDTO get(@PathParam("id") Long id);

  @Operation(
      summary = "Criar grupo",
      description = "Cria um novo grupo no sistema"
  )
  @APIResponses({
      @APIResponse(
          responseCode = "201",
          description = "Grupo criado com sucesso",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = GroupDTO.class)
          )
      ),
      @APIResponse(responseCode = "400", description = "Dados inválidos"),
      @APIResponse(responseCode = "401", description = "Não autorizado"),
      @APIResponse(responseCode = "403", description = "Sem permissão")
  })
  Response create(GroupDTO groupDTO);

  @Operation(
      summary = "Atualizar grupo",
      description = "Atualiza um grupo existente"
  )
  @APIResponses({
      @APIResponse(
          responseCode = "200",
          description = "Grupo atualizado com sucesso",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = GroupDTO.class)
          )
      ),
      @APIResponse(responseCode = "404", description = "Grupo não encontrado"),
      @APIResponse(responseCode = "400", description = "Dados inválidos"),
      @APIResponse(responseCode = "401", description = "Não autorizado"),
      @APIResponse(responseCode = "403", description = "Sem permissão")
  })
  GroupDTO update(@PathParam("id") Long id, GroupDTO groupDTO);

  @Operation(
      summary = "Excluir grupo",
      description = "Remove um grupo do sistema"
  )
  @APIResponses({
      @APIResponse(responseCode = "204", description = "Grupo excluído com sucesso"),
      @APIResponse(responseCode = "404", description = "Grupo não encontrado"),
      @APIResponse(responseCode = "409", description = "Grupo possui membros"),
      @APIResponse(responseCode = "401", description = "Não autorizado"),
      @APIResponse(responseCode = "403", description = "Sem permissão")
  })
  Response delete(@PathParam("id") Long id);

  @Operation(
      summary = "Adicionar membro ao grupo",
      description = "Adiciona um usuário a um grupo"
  )
  @APIResponses({
      @APIResponse(responseCode = "204", description = "Membro adicionado com sucesso"),
      @APIResponse(responseCode = "404", description = "Grupo ou usuário não encontrado"),
      @APIResponse(responseCode = "401", description = "Não autorizado"),
      @APIResponse(responseCode = "403", description = "Sem permissão")
  })
  Response addMember(
      @PathParam("id") Long groupId,
      @PathParam("userId") Long userId
  );

  @Operation(
      summary = "Remover membro do grupo",
      description = "Remove um usuário de um grupo"
  )
  @APIResponses({
      @APIResponse(responseCode = "204", description = "Membro removido com sucesso"),
      @APIResponse(responseCode = "404", description = "Grupo ou usuário não encontrado"),
      @APIResponse(responseCode = "401", description = "Não autorizado"),
      @APIResponse(responseCode = "403", description = "Sem permissão")
  })
  Response removeMember(
      @PathParam("id") Long groupId,
      @PathParam("userId") Long userId
  );


  @Operation(
      summary = "Atribuir permissão ao grupo",
      description = "Atribui uma permissão específica a um grupo"
  )
  @APIResponses({
      @APIResponse(responseCode = "204", description = "Permissão atribuída com sucesso"),
      @APIResponse(responseCode = "404", description = "Grupo não encontrado"),
      @APIResponse(responseCode = "400", description = "Permissão inválida"),
      @APIResponse(responseCode = "401", description = "Não autorizado"),
      @APIResponse(responseCode = "403", description = "Sem permissão")
  })
  Response addPermission(
      @PathParam("id") Long groupId,
      @QueryParam("permission") String permissionValue
  );

  @Operation(
      summary = "Remover permissão do grupo",
      description = "Remove uma permissão específica de um grupo"
  )
  @APIResponses({
      @APIResponse(responseCode = "204", description = "Permissão removida com sucesso"),
      @APIResponse(responseCode = "404", description = "Grupo não encontrado"),
      @APIResponse(responseCode = "400", description = "Permissão inválida"),
      @APIResponse(responseCode = "401", description = "Não autorizado"),
      @APIResponse(responseCode = "403", description = "Sem permissão")
  })
  Response removePermission(
      @PathParam("id") Long groupId,
      @QueryParam("permission") String permissionValue
  );
}
