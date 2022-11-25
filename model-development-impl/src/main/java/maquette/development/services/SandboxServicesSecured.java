package maquette.development.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.modules.users.model.UserAuthenticationToken;
import maquette.core.modules.users.services.UserServices;
import maquette.core.values.UID;
import maquette.core.values.user.User;
import maquette.development.values.WorkspaceMemberRole;
import maquette.development.values.sandboxes.Sandbox;
import maquette.development.values.sandboxes.SandboxProperties;
import maquette.development.values.sandboxes.volumes.VolumeDefinition;
import maquette.development.values.stacks.StackConfiguration;
import maquette.development.values.stacks.StackProperties;
import maquette.development.values.stacks.Stacks;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class SandboxServicesSecured implements SandboxServices {

    private final SandboxServices delegate;

    private final UserServices users;

    private final WorkspaceServicesCompanion workspaces;

    @Override
    public CompletionStage<SandboxProperties> createSandbox(User user, String workspace, String name, String comment,
                                                            Optional<VolumeDefinition> volume, List<StackConfiguration> stacks) {
        return workspaces
            .withAuthorization(
                /*
                 * The user needs to be authorized to create all stacks of the sandbox.
                 * Some stacks require specific global roles to be instantiated.
                 */
                () -> workspaces.isMember(user, workspace)
                    .thenCompose(isMember -> Operators
                        .allOf(stacks.stream().map(stackConfiguration -> {
                            var stack = Stacks.apply().getStackByConfiguration(stackConfiguration);

                            return stack
                                .getRequiredRole()
                                .map(role -> users.hasGlobalRole(user, role))
                                .orElse(CompletableFuture.completedFuture(Boolean.TRUE));
                        }))
                        .thenApply(results -> {
                            // If one of the stacks is not allowed to be created by the user, we don't allow the creation
                            // of the sandbox.
                            return isMember && !results.contains(Boolean.FALSE);
                        }))
            )
            .thenCompose(ok -> delegate.createSandbox(user, workspace, name, comment, volume, stacks));
    }

    @Override
    public CompletionStage<UserAuthenticationToken> getAuthenticationToken(
        UID workspaceId, UID sandboxId, String stackHash) {

        return delegate.getAuthenticationToken(workspaceId, sandboxId, stackHash);
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
