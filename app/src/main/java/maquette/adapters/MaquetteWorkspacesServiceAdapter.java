package maquette.adapters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.core.values.UID;
import maquette.core.values.user.User;
import maquette.datashop.ports.Workspace;
import maquette.datashop.ports.WorkspacesServicePort;
import maquette.development.MaquetteModelDevelopment;
import maquette.development.entities.WorkspaceEntity;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class MaquetteWorkspacesServiceAdapter implements WorkspacesServicePort {

    private final ObjectMapper om;

    private MaquetteModelDevelopment module;

    public static MaquetteWorkspacesServiceAdapter apply(ObjectMapper om) {
        return new MaquetteWorkspacesServiceAdapter(om, null);
    }

    @Override
    public CompletionStage<UID> getWorkspaceIdByName(String workspaceName) {
        if (Objects.isNull(module)) {
            throw NotInitializedException.apply(MaquetteModelDevelopment.MODULE_NAME);
        }

        return module
            .getEntities()
            .getWorkspaceByName(workspaceName)
            .thenApply(WorkspaceEntity::getId);
    }

    @Override
    public CompletionStage<List<Workspace>> getWorkspacesByMember(User user) {
        if (Objects.isNull(module)) {
            throw NotInitializedException.apply(MaquetteModelDevelopment.MODULE_NAME);
        }

        return module
            .getEntities()
            .getWorkspacesByMember(user)
            .thenApply(workspaces -> workspaces
                .stream()
                .map(props -> Workspace.apply(props.getId(), props.getName(), props.getTitle()))
                .collect(Collectors.toList()));
    }

    @Override
    public CompletionStage<JsonNode> getWorkspacePropertiesByWorkspaceId(UID id) {
        if (Objects.isNull(module)) {
            throw NotInitializedException.apply(MaquetteModelDevelopment.MODULE_NAME);
        }

        return module
            .getEntities()
            .getWorkspaceById(id)
            .thenCompose(WorkspaceEntity::getProperties)
            .thenApply(workspace -> om.convertValue(workspace, JsonNode.class));
    }

    public void setMaquetteModule(MaquetteModelDevelopment module) {
        this.module = module;
    }

}
