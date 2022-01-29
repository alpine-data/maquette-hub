package maquette.development.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.values.user.User;
import maquette.development.values.WorkspaceMemberRole;
import maquette.development.values.sandboxes.Sandbox;
import maquette.development.values.sandboxes.SandboxProperties;
import maquette.development.values.sandboxes.volumes.VolumeDefinition;
import maquette.development.values.stacks.StackConfiguration;
import maquette.development.values.stacks.StackProperties;

import java.util.List;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class SandboxServicesSecured implements SandboxServices {

    private final SandboxServices delegate;

    private final WorkspaceServicesCompanion workspaces;

    @Override
    public CompletionStage<SandboxProperties> createSandbox(User user, String workspace, String name, String comment,
                                                            VolumeDefinition volume, List<StackConfiguration> stacks) {
        return workspaces
            .withAuthorization(() -> workspaces.isMember(user, workspace))
            .thenCompose(ok -> delegate.createSandbox(user, workspace, name, comment, volume, stacks));
    }

    @Override
    public CompletionStage<Sandbox> getSandbox(User user, String workspace, String sandbox) {
        return workspaces
            .withAuthorization(() -> workspaces.isMember(user, workspace))
            .thenCompose(ok -> delegate.getSandbox(user, workspace, sandbox))
            .thenCompose(sdbx -> workspaces
                .withAuthorization(
                    () -> workspaces.isMember(user, workspace, WorkspaceMemberRole.ADMIN),
                    () -> workspaces.isSandboxOwner(user, sdbx.getProperties()))
                .thenApply(ok -> sdbx));
    }

    @Override
    public CompletionStage<List<StackProperties>> getStacks(User user) {
        return delegate.getStacks(user);
    }

    @Override
    public CompletionStage<List<SandboxProperties>> getSandboxes(User user, String workspace) {
        return workspaces
            .withAuthorization(() -> workspaces.isMember(user, workspace))
            .thenCompose(ok -> delegate.getSandboxes(user, workspace))
            .thenCompose(sandboxes -> Operators.allOf(sandboxes
                .stream()
                .map(sdbx -> workspaces.filterAuthorized(
                    sdbx,
                    () -> workspaces.isMember(user, workspace, WorkspaceMemberRole.ADMIN),
                    () -> workspaces.isSandboxOwner(user, sdbx)))
            ))
            .thenApply(Operators::filterOptional);
    }

    @Override
    public CompletionStage<Done> removeSandbox(User user, String workspace, String sandbox) {
        return workspaces
            .withAuthorization(
                () -> workspaces.isMember(user, workspace, WorkspaceMemberRole.ADMIN),
                () -> workspaces.isSandboxOwner(user, workspace, sandbox))
            .thenCompose(ok -> delegate.removeSandbox(user, workspace, sandbox));
    }

}
