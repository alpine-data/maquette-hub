package maquette.development.ports;

import akka.Done;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import maquette.core.values.UID;
import maquette.development.values.sandboxes.SandboxProperties;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class InMemorySandboxesRepository implements SandboxesRepository {

    private final Map<UID, SandboxProperties> sandboxes;

    public static InMemorySandboxesRepository apply() {
        return apply(Maps.newHashMap());
    }

    @Override
    public CompletionStage<Optional<SandboxProperties>> findSandboxById(UID workspace, UID sandbox) {
        var result = Optional.ofNullable(sandboxes.get(sandbox));
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<Optional<SandboxProperties>> findSandboxByName(UID workspace, String sandbox) {
        var result = sandboxes
            .values()
            .stream()
            .filter(sdbx -> sdbx
                .getWorkspace()
                .equals(workspace) && sdbx
                .getName()
                .equals(sandbox))
            .findFirst();

        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<Done> insertOrUpdateSandbox(UID workspace, SandboxProperties sandbox) {
        this.sandboxes.remove(sandbox.getId());
        this.sandboxes.put(sandbox.getId(), sandbox);
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<List<SandboxProperties>> listSandboxes(UID workspace) {
        var result = sandboxes
            .values()
            .stream()
            .filter(sdbx -> sdbx
                .getWorkspace()
                .equals(workspace))
            .collect(Collectors.toList());

        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<Done> removeSandboxById(UID workspace, UID sandbox) {
        this.sandboxes.remove(sandbox);
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<Done> removeSandboxByName(UID workspace, String name) {
        return findSandboxByName(workspace, name).thenApply(sdbx -> {
            sdbx.ifPresent(properties -> this.sandboxes.remove(properties.getId()));
            return Done.getInstance();
        });
    }

}
