package maquette.development.ports.infrastructure.docker;

import akka.Done;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import maquette.core.common.Operators;
import maquette.development.ports.infrastructure.docker.model.DeploymentConfig;
import maquette.development.ports.infrastructure.docker.model.DeploymentProperties;
import maquette.development.ports.infrastructure.docker.model.DeploymentStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@With
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Deployment {

    private final DeploymentConfig config;

    private final List<DockerContainer> containers;

    private final Instant created;

    private DeploymentStatus status;

    public static Deployment apply(DeploymentConfig config, List<DockerContainer> containers, Instant created) {
        return new Deployment(config, List.copyOf(containers), Instant.now(), DeploymentStatus.STARTED);
    }

    public Optional<DockerContainer> findContainer(String name) {
        return containers
            .stream()
            .filter(c -> c
                .getConfig()
                .getName()
                .equals(name))
            .findFirst();
    }

    public DockerContainer getContainer(String name) {
        return findContainer(name).orElseThrow(() -> new RuntimeException("Container not found ..."));
    }

    public CompletionStage<DeploymentProperties> getProperties() {
        return Operators
            .allOf(containers
                .stream()
                .map(DockerContainer::getProperties)
                .collect(Collectors.toList()))
            .thenApply(containerProperties -> DeploymentProperties.apply(config, containerProperties, created, status));
    }

    public CompletionStage<Done> stop() {
        return Operators
            .allOf(containers
                .stream()
                .map(DockerContainer::stop))
            .thenApply(done -> {
                status = DeploymentStatus.STOPPED;
                return Done.getInstance();
            });
    }

    public CompletionStage<Done> remove() {
        return stop()
            .thenCompose(done -> Operators.allOf(containers
                .stream()
                .map(DockerContainer::remove)))
            .thenApply(done -> {
                return Done.getInstance();
            });
    }

    public CompletionStage<Done> start() {
        return Operators
            .allOf(containers
                .stream()
                .map(DockerContainer::start)
                .collect(Collectors.toList()))
            .thenApply(done -> {
                status = DeploymentStatus.STARTED;
                return Done.getInstance();
            });
    }

}
