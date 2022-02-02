package maquette.development.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.modules.ServicesCompanion;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;
import maquette.development.entities.SandboxEntities;
import maquette.development.entities.SandboxEntity;
import maquette.development.entities.WorkspaceEntities;
import maquette.development.values.WorkspaceMemberRole;
import maquette.development.values.sandboxes.SandboxProperties;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class WorkspaceServicesCompanion extends ServicesCompanion {

    private final WorkspaceEntities workspaces;

    private final SandboxEntities sandboxes;

    public <T> CompletionStage<Optional<T>> filterMember(User user, String name, T passThrough) {
        return filterMember(user, name, null, passThrough);
    }

    public <T> CompletionStage<Optional<T>> filterMember(User user, String name, WorkspaceMemberRole role,
                                                         T passThrough) {
        return workspaces.getWorkspaceByName(name)
            .thenCompose(workspace -> workspace.isMember(user, role))
            .thenApply(auth -> {
                if (auth) {
                    return Optional.of(passThrough);
                } else {
                    return Optional.empty();
                }
            });
    }

    public CompletionStage<Boolean> isMember(User user, String name) {
        return isMember(user, name, null);
    }

    public CompletionStage<Boolean> isMember(User user, String name, WorkspaceMemberRole role) {
        return filterMember(user, name, role, Done.getInstance()).thenApply(Optional::isPresent);
    }

    public CompletionStage<Boolean> isSandboxOwner(User user, SandboxProperties sandbox) {
        return CompletableFuture.completedFuture(user instanceof AuthenticatedUser && ((AuthenticatedUser) user).getId()
            .getValue()
            .equals(sandbox.getCreated().getBy()));
    }

    public CompletionStage<Boolean> isSandboxOwner(User user, String workspace, String sandbox) {
        return workspaces
            .getWorkspaceByName(workspace)
            .thenCompose(wks -> sandboxes.getSandboxByName(wks.getId(), sandbox))
            .thenCompose(SandboxEntity::getProperties)
            .thenCompose(sdbx -> isSandboxOwner(user, sdbx));
    }

}
