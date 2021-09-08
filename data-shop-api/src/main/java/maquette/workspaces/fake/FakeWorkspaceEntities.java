package maquette.workspaces.fake;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import maquette.core.values.UID;
import maquette.core.values.user.User;
import maquette.workspaces.api.WorkspaceEntities;
import maquette.workspaces.api.WorkspaceEntity;
import maquette.workspaces.api.WorkspaceProperties;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class FakeWorkspaceEntities implements WorkspaceEntities {

    private final List<WorkspaceProperties> workspaces;

    public static FakeWorkspaceEntities apply() {
        return apply(Lists.newArrayList());
    }

    public void addWorkspace(WorkspaceProperties workspace) {
        this.workspaces.add(workspace);
    }

    @Override
    public CompletionStage<WorkspaceEntity> getWorkspaceByName(String name) {
        var result = workspaces
            .stream()
            .filter(w -> w.getName().equals(name))
            .findFirst()
            .orElseThrow();

        return CompletableFuture.completedFuture(FakeWorkspaceEntity.apply(result));
    }

    @Override
    public CompletionStage<WorkspaceEntity> getWorkspaceById(UID id) {
        var result = workspaces
            .stream()
            .filter(w -> w.getId().equals(id))
            .findFirst()
            .orElseThrow();

        return CompletableFuture.completedFuture(FakeWorkspaceEntity.apply(result));
    }

    @Override
    public CompletionStage<List<WorkspaceEntity>> getWorkspacesByMember(User user) {
        return CompletableFuture.completedFuture(List.of());
    }

}
