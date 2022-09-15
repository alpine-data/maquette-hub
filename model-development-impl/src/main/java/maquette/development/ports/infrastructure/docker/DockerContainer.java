package maquette.development.ports.infrastructure.docker;

import akka.Done;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.ContainerPort;
import com.github.dockerjava.api.model.Frame;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.development.ports.infrastructure.docker.model.ContainerConfig;
import maquette.development.ports.infrastructure.docker.model.ContainerProperties;
import maquette.development.ports.infrastructure.docker.model.ContainerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class DockerContainer {

    private final Logger LOG = LoggerFactory.getLogger(DockerContainer.class);

    private final DockerClient client;

    private final DockerVolumeCompanion volumes;

    private final ContainerConfig config;

    private final String containerId;

    private Optional<com.github.dockerjava.api.model.Container> getContainer() {
        return client
            .listContainersCmd()
            .withShowAll(true)
            .exec()
            .stream()
            .filter(container -> container
                .getId()
                .equals(containerId))
            .findFirst();
    }

    public ContainerConfig getConfig() {
        return config;
    }

    public CompletionStage<ContainerStatus> getStatus() {
        var result = getContainer()
            .map(container -> {
                var status = container
                    .getStatus()
                    .toLowerCase();

                if (status.contains("up")) {
                    return ContainerStatus.RUNNING;
                } else if (status.contains("exited (0)")) {
                    return ContainerStatus.STOPPED;
                } else if (status.contains("exited")) {
                    return ContainerStatus.FAILED;
                } else {
                    return ContainerStatus.CREATED;
                }
            })
            .orElse(ContainerStatus.STOPPED);

        return CompletableFuture.completedFuture(result);
    }

    public CompletionStage<Map<Integer, URL>> getMappedPortUrls() {
        var result = getContainer()
            .map(container -> Arrays
                .stream(container.ports)
                .collect(Collectors.toMap(
                    ContainerPort::getPrivatePort,
                    p -> Operators.suppressExceptions(() -> new URL("http://localhost:" + p.getPublicPort())))))
            .orElse(Maps.newHashMap());

        return CompletableFuture.completedFuture(result);
    }

    public CompletionStage<String> getLogs() {
        client
            .logContainerCmd(containerId)
            .exec(new ResultCallback<Frame>() {
                @Override
                public void onStart(Closeable closeable) {

                }

                @Override
                public void onNext(Frame object) {
                    System.out.println(Arrays.toString(object.getPayload()));
                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onComplete() {

                }

                @Override
                public void close() {

                }
            });

        return CompletableFuture.completedFuture("");
    }

    public CompletionStage<Done> start() {
        DockerOperations
            .apply(client, volumes, LOG)
            .startContainer(config, containerId);

        return CompletableFuture.completedFuture(Done.getInstance());
    }

    public CompletionStage<Done> stop() {
        DockerOperations
            .apply(client, volumes, LOG)
            .stopContainer(containerId);

        return CompletableFuture.completedFuture(Done.getInstance());
    }

    public CompletionStage<Done> remove() {
        DockerOperations
            .apply(client, volumes, LOG)
            .removeContainer(containerId);

        return CompletableFuture.completedFuture(Done.getInstance());
    }

    public CompletionStage<ContainerProperties> getProperties() {
        return Operators.compose(getStatus(), getMappedPortUrls(), (status, urls) ->
            ContainerProperties.apply(config, status, urls));
    }

}
