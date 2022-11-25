package maquette.operations.ports;

import akka.Done;
import maquette.operations.value.DeployedModel;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDeployedModelsRepository implements DeployedModelsRepository {

    static final Map<String, DeployedModel> models = new ConcurrentHashMap<>();

    @Override
    public CompletionStage<Optional<DeployedModel>> findByName(String name) {
        return CompletableFuture.completedFuture(Optional.ofNullable(models.get(name)));
    }

    @Override
    public CompletionStage<Done> insertOrUpdate(String name, String title, String url) {
        models.put(name, models.getOrDefault(name, DeployedModel.apply(name, title, url, List.of())));
        return CompletableFuture.completedFuture(Done.getInstance());
    }
}
