package maquette.operations.ports;

import akka.Done;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import maquette.operations.value.DeployedModelService;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor(staticName = "apply")
public final class InMemoryDeployedModelServicesRepository implements DeployedModelServicesRepository {

    private List<DeployedModelService> services;

    public static InMemoryDeployedModelServicesRepository apply() {
        return apply(Lists.newArrayList());
    }

    @Override
    public CompletionStage<Optional<DeployedModelService>> findByNameAndGitUrl(String serviceName, String gitUrl) {
        return CompletableFuture.completedFuture(services
            .stream()
            .filter(service -> service.getName().equals(serviceName) && service.getGitRepositoryUrl().equals(gitUrl))
            .findFirst());
    }

    @Override
    public CompletionStage<List<DeployedModelService>> findByModelUrl(String modelUrl) {
        return CompletableFuture.completedFuture(services
            .stream()
            .filter(service -> service
                .getInstances()
                .stream()
                .anyMatch(instance -> instance
                    .getModels()
                    .stream()
                    .anyMatch(model -> model.getModelUrl().equals(modelUrl))))
            .collect(Collectors.toList()));
    }

    @Override
    public CompletionStage<Done> insertOrUpdate(DeployedModelService service) {
        this.services = Stream
            .concat(
                services
                    .stream()
                    .filter(existingService -> !(existingService.sameIdentity(service))),
                Stream.of(service))
            .collect(Collectors.toList());

        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<Done> removeByNameAndGitUrl(String serviceName, String gitUrl) {
        this.services = services
            .stream()
            .filter(existingService -> !(existingService.sameIdentity(serviceName, gitUrl)))
            .collect(Collectors.toList());

        return CompletableFuture.completedFuture(Done.getInstance());
    }

}
