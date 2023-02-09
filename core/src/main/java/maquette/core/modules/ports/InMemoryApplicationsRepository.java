package maquette.core.modules.ports;

import akka.Done;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import maquette.core.modules.applications.model.Application;
import maquette.core.values.UID;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public class InMemoryApplicationsRepository implements ApplicationsRepository {
    private final Map<UID, Application> applications;

    public static InMemoryApplicationsRepository apply() {
        return apply(Maps.newConcurrentMap());
    }

    @Override
    public CompletionStage<Optional<Application>> findByIdAndSecret(UID id, String secret) {
        Application app = applications.get(id);
        if (app == null || !app.getSecret().equals(secret)) return CompletableFuture.completedFuture(Optional.empty());
        return CompletableFuture.completedFuture(Optional.of(app));
    }

    @Override
    public CompletionStage<Optional<Application>> findById(UID id) {
        Application app = applications.get(id);
        if (app == null) return CompletableFuture.completedFuture(Optional.empty());
        return CompletableFuture.completedFuture(Optional.of(app));
    }

    @Override
    public CompletionStage<Optional<Application>> findByNameAndWorkspaceId(String name, UID workspaceId) {
        return findByWorkspaceId(workspaceId)
            .thenApply(
                results ->
                    results
                        .stream()
                        .filter(result -> result.getName().equals(name))
                        .findFirst()
            );
    }

    @Override
    public CompletionStage<List<Application>> findByWorkspaceId(UID workspaceId) {
        return CompletableFuture.completedFuture(
            applications
                .values()
                .stream()
                .filter(application -> application.getWorkspaceId().equals(workspaceId))
                .collect(Collectors.toList())
        );
    }

    @Override
    public CompletionStage<Done> save(Application application) {
        applications.put(application.getId(), application);
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<Done> remove(Application application) {
        applications.remove(application.getId());
        return CompletableFuture.completedFuture(Done.getInstance());
    }
}
