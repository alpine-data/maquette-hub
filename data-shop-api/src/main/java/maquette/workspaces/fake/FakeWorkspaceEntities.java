package maquette.workspaces.fake;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.authorization.WildcardAuthorization;
import maquette.core.values.user.User;
import maquette.workspaces.api.ProjectMemberRole;
import maquette.workspaces.api.WorkspaceEntities;
import maquette.workspaces.api.WorkspaceEntity;
import maquette.workspaces.api.WorkspaceProperties;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class FakeWorkspaceEntities implements WorkspaceEntities {

    private final List<WorkspaceProperties> workspaces;

    private final Map<UID, List<GrantedAuthorization<ProjectMemberRole>>> authorizations;

    public static FakeWorkspaceEntities apply() {
        return apply(Lists.newArrayList(), Maps.newHashMap());
    }

    public void addWorkspace(WorkspaceProperties workspace) {
        if (!authorizations.containsKey(workspace.getId())) {
            this.workspaces.add(workspace);
            this.authorizations.put(workspace.getId(), Lists.newArrayList());
            this.authorizations.get(workspace.getId()).add(GrantedAuthorization.apply(
                ActionMetadata.apply("system"),
                WildcardAuthorization.apply(),
                ProjectMemberRole.ADMIN));
        }
    }

    @Override
    public CompletionStage<WorkspaceEntity> getWorkspaceByName(String name) {
        addWorkspace(WorkspaceProperties.apply(name));

        var result = workspaces
            .stream()
            .filter(w -> w.getName().equals(name))
            .findFirst()
            .orElseGet(() -> {
                var workspace = WorkspaceProperties.apply(name);
                addWorkspace(workspace);

                return workspace;
            });

        return CompletableFuture.completedFuture(FakeWorkspaceEntity.apply(result));
    }

    @Override
    public CompletionStage<WorkspaceEntity> getWorkspaceById(UID id) {
        addWorkspace(WorkspaceProperties.apply(id, "some name"));

        var result = workspaces
            .stream()
            .filter(w -> w.getId().equals(id))
            .findFirst()
            .orElseThrow();

        return CompletableFuture.completedFuture(FakeWorkspaceEntity.apply(result));
    }

    @Override
    public CompletionStage<List<WorkspaceEntity>> getWorkspacesByMember(User user) {
        return CompletableFuture.completedFuture(workspaces
            .stream()
            .map(FakeWorkspaceEntity::apply)
            .collect(Collectors.toList()));
    }

}
