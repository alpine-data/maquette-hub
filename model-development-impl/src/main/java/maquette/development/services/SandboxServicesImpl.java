package maquette.development.services;

import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.values.UID;
import maquette.core.values.user.User;
import maquette.development.entities.SandboxEntities;
import maquette.development.entities.SandboxEntity;
import maquette.development.entities.WorkspaceEntities;
import maquette.development.entities.WorkspaceEntity;
import maquette.development.values.sandboxes.Sandbox;
import maquette.development.values.sandboxes.SandboxProperties;
import maquette.development.values.sandboxes.volumes.VolumeDefinition;
import maquette.development.values.stacks.StackConfiguration;
import maquette.development.values.stacks.StackProperties;
import maquette.development.values.stacks.Stacks;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;

@AllArgsConstructor(staticName = "apply")
public final class SandboxServicesImpl implements SandboxServices {

    private final SandboxEntities sandboxes;

    private final WorkspaceEntities workspaces;

    private final WorkspaceServices workspaceServices;

    @Override
    public CompletionStage<SandboxProperties> createSandbox(
        User user, String workspace, String name, String comment, VolumeDefinition volume, List<StackConfiguration> stacks) {

        return workspaces
            .getWorkspaceByName(workspace)
            .thenCompose(wks -> sandboxes.createSandbox(user, wks.getId(), UID.apply(), name, comment))
            .thenCompose(sdbx -> sandboxes.getSandboxById(sdbx.getWorkspace(), sdbx.getId()))
            .thenCompose(sdbx -> sdbx.addStacks(stacks).thenCompose(d -> sdbx.getProperties()));
    }

    @Override
    public CompletionStage<Sandbox> getSandbox(User user, String workspace, String sandbox) {
        return withSandboxByName(workspace, sandbox, (wks, sdbx) -> {
            var propertiesCS = sdbx.getProperties();
            var paramsCS = sdbx.getStackInstanceParameters();

            return Operators.compose(propertiesCS, paramsCS, Sandbox::apply);
        });
    }

    @Override
    public CompletionStage<List<StackProperties>> getStacks(User user) {
        return CompletableFuture.completedFuture(Stacks.apply().getStacks());
    }

    @Override
    public CompletionStage<List<SandboxProperties>> getSandboxes(User user, String workspace) {
        return workspaces.getWorkspaceByName(workspace).thenCompose(wks -> sandboxes.listSandboxes(wks.getId()));
    }

    private <T> CompletionStage<T> withSandboxByName(String workspace, String sandbox, BiFunction<WorkspaceEntity, SandboxEntity, CompletionStage<T>> func) {
        return workspaces.getWorkspaceByName(workspace)
            .thenCompose(project -> sandboxes
                .getSandboxByName(project.getId(), sandbox)
                .thenCompose(sdbx -> func.apply(project, sdbx)));
    }

}
