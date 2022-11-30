package maquette.operations.ports;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.operations.value.DeployedModel;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public class InMemoryDeployedModelsRepository implements DeployedModelsRepository {

    static final Map<String, DeployedModel> models = new ConcurrentHashMap<>();
    static final Map<String, Set<String>> serviceReferences = new ConcurrentHashMap<>();

    @Override
    public CompletionStage<List<DeployedModel>> findByName(String name) {
        return CompletableFuture.completedFuture(models
            .values()
            .stream()
            .filter(model -> model.getName().equals(name))
            .collect(Collectors.toList())
        );
    }

    @Override
    public CompletionStage<Optional<DeployedModel>> findByUrl(String url) {
        return CompletableFuture.completedFuture(Optional.ofNullable(models.get(url)));
    }

    @Override
    public CompletionStage<Done> insertOrUpdate(String name, String title, String url) {
        models.put(url, models.getOrDefault(url, DeployedModel.apply(name, title, url, List.of())));
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<Set<String>> findServiceReferences(String modelUrl) {
        serviceReferences.putIfAbsent(modelUrl, new HashSet<>());
        return CompletableFuture.completedFuture(serviceReferences.get(modelUrl));
    }

    @Override
    public CompletionStage<Done> assignServices(String modelUrl, Set<String> serviceNames) {
        serviceReferences.putIfAbsent(modelUrl, new HashSet<>());
        serviceReferences.get(modelUrl).addAll(serviceNames);
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<Done> removeServices(String modelUrl, Set<String> serviceNames) {
        if (serviceReferences.containsKey(modelUrl)) {
            serviceReferences.get(modelUrl).removeAll(serviceNames);
        }
        return CompletableFuture.completedFuture(Done.getInstance());
    }
}
