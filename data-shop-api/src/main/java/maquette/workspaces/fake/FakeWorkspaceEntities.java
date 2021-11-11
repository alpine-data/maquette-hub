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
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class FakeWorkspaceEntities implements WorkspaceEntities {

    private final List<WorkspaceProperties> workspaces;

    public static FakeWorkspaceEntities apply() {
        var workspaces = Lists.newArrayList(
            WorkspaceProperties.apply(UID.apply("alice-a"), "alice-project-a", Lists.newArrayList("alice")),
            WorkspaceProperties.apply(UID.apply("bobs-a"), "bobs-project-a", Lists.newArrayList("bob")),
            WorkspaceProperties.apply(UID.apply("clairs-a"), "clair-project-a", Lists.newArrayList("clair")),
            WorkspaceProperties.apply(UID.apply("michaels-a"), "michaels-project-a", Lists.newArrayList("michael")));

        return apply(workspaces);
    }

    @Override
    public CompletionStage<WorkspaceEntity> getWorkspaceByName(String name) {
        var result = workspaces
            .stream()
            .filter(w -> w.getName().equals(name))
            .findFirst()
            .orElseThrow(() -> new RuntimeException(String.format("Workspace with name %s not found", name)));

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
        return CompletableFuture.completedFuture(workspaces
            .stream()
            .filter(wks -> wks.getMembers().contains(user.getDisplayName()))
            .map(FakeWorkspaceEntity::apply)
            .collect(Collectors.toList()));
    }

}
