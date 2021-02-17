package maquette.core.values.user;

import maquette.core.values.authorization.Authorization;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public interface User {

    String getDisplayName();

    List<String> getRoles();

    Authorization toAuthorization();

    Optional<ProjectContext> getProjectContext();

    Optional<EnvironmentContext> getEnvironmentContext();

    default boolean isSystemUser() {
        return false;
    }

    default CompletionStage<Boolean> isSystemUserCS() {
        return CompletableFuture.completedFuture(isSystemUser());
    }

}
