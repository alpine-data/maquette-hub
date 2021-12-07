package maquette.development.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.modules.ServicesCompanion;
import maquette.core.values.user.User;
import maquette.development.entities.WorkspaceEntities;
import maquette.development.values.WorkspaceMemberRole;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class WorkspaceServicesCompanion extends ServicesCompanion {

    private final WorkspaceEntities workspaces;

    public <T> CompletionStage<Optional<T>> filterMember(User user, String name, T passThrough) {
        return filterMember(user, name, null, passThrough);
    }

    public <T> CompletionStage<Optional<T>> filterMember(User user, String name, WorkspaceMemberRole role, T passThrough) {
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

}
