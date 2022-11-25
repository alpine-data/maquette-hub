package maquette.operations.ports;

import akka.Done;
import maquette.operations.value.DeployedModelInstance;
import maquette.operations.value.DeployedModelService;
import maquette.operations.value.DeployedModelServiceProperties;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryDeployedModelServicesRepository implements DeployedModelServicesRepository {
    static final Map<String, DeployedModelService> services = new ConcurrentHashMap<>();

    public CompletionStage<Optional<DeployedModelServiceProperties>> findByName(String serviceName) {
        return CompletableFuture.completedFuture(Optional.ofNullable(services.get(serviceName))
            .map(DeployedModelService::getProperties));
    }

    public CompletionStage<Done> insertOrUpdate(DeployedModelServiceProperties deployedModelServiceProperties) {
        services.put(
            deployedModelServiceProperties.getName(),
            services
                .getOrDefault(deployedModelServiceProperties.getName(),
                    DeployedModelService.apply(deployedModelServiceProperties, List.of()))
                .withProperties(deployedModelServiceProperties)
        );
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    public CompletionStage<Done> removeByName(String serviceName) {
        services.remove(serviceName);
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    public CompletionStage<List<DeployedModelInstance>> findAllInstances(String serviceName) {
        final var service = services.get(serviceName);
        return CompletableFuture.completedFuture(service == null ? List.of() : service.getInstances());
    }

    public CompletionStage<Done> removeAllInstances(String serviceName) {
        final var service = services.get(serviceName);
        services.put(serviceName, service.withInstances(List.of()));
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    public CompletionStage<Optional<DeployedModelInstance>> findInstanceByUrl(String serviceName, String url) {
        return findAllInstances(serviceName).thenApply(
            instances -> instances
                .stream()
                .filter(instance -> url.equals(instance.getUrl()))
                .findFirst()
        );
    }

    public CompletionStage<Done> insertOrUpdateInstance(String serviceName, DeployedModelInstance instance) {
        final DeployedModelService service = Objects.requireNonNull(services.get(serviceName));
        final var instances = service.getInstances()
            .stream()
            .filter(inst -> !inst.getUrl().equals(instance.getUrl()))
            .collect(Collectors.toList());
        instances.add(instance);
        services.put(serviceName, service.withInstances(instances));
        return CompletableFuture.completedFuture(Done.getInstance());
    }
}
