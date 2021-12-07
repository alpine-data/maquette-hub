package maquette.datashop.ports;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.common.exceptions.ApplicationException;
import maquette.core.databind.DefaultObjectMapperFactory;
import maquette.core.values.UID;
import maquette.core.values.user.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class FakeWorkspacesServicePort implements WorkspacesServicePort {

    private final ObjectMapper om;

    private final List<FakeWorkspace> workspaces;

    public static FakeWorkspacesServicePort apply() {
        var om = DefaultObjectMapperFactory.apply().createJsonMapper();

        var workspaces = Lists.newArrayList(
            FakeWorkspace.apply(UID.apply("alice-a"), "alice-project-a", List.of("alice")),
            FakeWorkspace.apply(UID.apply("bobs-a"), "bobs-project-a", List.of("bob")),
            FakeWorkspace.apply(UID.apply("clairs-a"), "clairs-project-a", List.of("clair")),
            FakeWorkspace.apply(UID.apply("michaels-a"), "michaels-project-a", List.of("michael")));

        return apply(om, workspaces);
    }

    @Override
    public CompletionStage<UID> getWorkspaceIdByName(String workspaceName) {
        var result = workspaces
            .stream()
            .filter(workspace -> workspace.name.equals(workspaceName))
            .map(workspace -> workspace.id)
            .findFirst()
            .orElseThrow(() -> WorkspaceNotFoundException.apply(workspaceName));

        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<List<Workspace>> getWorkspacesByMember(User user) {
        var result = workspaces
            .stream()
            .filter(workspace -> workspace.getMembers().contains(user.getDisplayName()))
            .map(workspace -> Workspace.apply(workspace.getId(), workspace.name, workspace.name))
            .collect(Collectors.toList());

        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<JsonNode> getWorkspacePropertiesByWorkspaceId(UID id) {
        var result = workspaces
            .stream()
            .filter(workspace -> workspace.id.equals(id))
            .map(workspace -> om.convertValue(workspace, JsonNode.class))
            .findFirst()
            .orElseThrow(() -> WorkspaceNotFoundException.apply(id.getValue()));

        return CompletableFuture.completedFuture(result);
    }

    /**
     * Method to setup dummy test data. Registers a new workspace if not present, if the workspace does not exist yet,
     * it will be created with the related owner.
     *
     * @param owner         The owner of the newly created workspace.
     * @param workspaceName The name of the workspace.
     */
    public void createWorkspaceIfNotPresent(User owner, String workspaceName) {
        var exists = workspaces
            .stream()
            .filter(wks -> wks.getName().equals(workspaceName))
            .findFirst();

        if (exists.isPresent()) {
            exists.get().getMembers().add(owner.getDisplayName());
        } else {
            workspaces.add(FakeWorkspace.apply(UID.apply(workspaceName), workspaceName,
                Lists.newArrayList(owner.getDisplayName())));
        }
    }

    @Value
    @AllArgsConstructor(staticName = "apply")
    private static class FakeWorkspace {

        UID id;

        String name;

        List<String> members;

    }

    private static class WorkspaceNotFoundException extends ApplicationException {

        private WorkspaceNotFoundException(String message) {
            super(message);
        }

        public static WorkspaceNotFoundException apply(String workspace) {
            return new WorkspaceNotFoundException("Workspace `" + workspace + "` not found.");
        }

    }

}
