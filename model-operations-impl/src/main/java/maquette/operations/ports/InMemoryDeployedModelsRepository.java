package maquette.operations.ports;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.operations.value.DeployedModel;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor(staticName = "apply")
public class InMemoryDeployedModelsRepository implements DeployedModelsRepository {

    static final Map<String, DeployedModel> models = new ConcurrentHashMap<>();
    static final Map<String, Set<String>> serviceReferences = new ConcurrentHashMap<>();

    @Override
    public CompletionStage<Optional<DeployedModel>> findByName(String name) {
        return CompletableFuture.completedFuture(Optional.ofNullable(models.get(name)));
    }

    @Override
    public CompletionStage<Done> insertOrUpdate(String name, String title, String url) {
        models.put(name, models.getOrDefault(name, DeployedModel.apply(name, title, url, List.of())));
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<Set<String>> findServiceReferences(String name) {
        serviceReferences.putIfAbsent(name, new HashSet<>());
        return CompletableFuture.completedFuture(serviceReferences.get(name));
    }

    @Override
    public CompletionStage<Done> assignServices(String name, Set<String> serviceNames) {
        serviceReferences.putIfAbsent(name, new HashSet<>());
        serviceReferences.get(name).addAll(serviceNames);
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<Done> removeServices(String name, Set<String> serviceNames) {
        if (serviceReferences.containsKey(name)) {
            serviceReferences.get(name).removeAll(serviceNames);
        }
        return CompletableFuture.completedFuture(Done.getInstance());
    }
}
