package maquette.adapters.infrastructure;

import akka.Done;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import maquette.core.entities.infrastructure.Container;
import maquette.core.entities.infrastructure.model.ContainerConfig;
import maquette.core.entities.infrastructure.model.ContainerStatus;
import maquette.core.ports.InfrastructureProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DockerInfrastructureProvider implements InfrastructureProvider {

    private static final Logger LOG = LoggerFactory.getLogger(DockerInfrastructureProvider.class);

    private final DockerClient client;

    public static DockerInfrastructureProvider apply() {
        DockerClientConfig config = DefaultDockerClientConfig
                .createDefaultConfigBuilder()
                .build();

        DockerHttpClient httpClient = new ZerodepDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .build();

        return apply(DockerClientImpl.getInstance(config, httpClient));
    }

    @Override
    public CompletionStage<Container> createContainer(ContainerConfig config) {
        var callback = CompletionStageResultCallback.<PullResponseItem>apply(
           String.format("docker pull %s", config.getImage()), LOG);

        client.pullImageCmd("hello-world").exec(callback);

        return callback
           .result()
           .thenApply(done -> client
              .createContainerCmd(config.getImage())
              .withName(config.getName())
              .exec())
           .thenApply(createContainerResponse -> {
               Arrays
                  .stream(createContainerResponse.getWarnings())
                  .map(s -> String.format("`docker create %s` - %s", config.getImage(), s))
                  .forEach(LOG::warn);

               LOG.info("`docker create {}` - Completed with id `{}`", config.getImage(), createContainerResponse.getId());

               return createContainerResponse.getId();
           })
           .thenApply(containerId -> {
               client.startContainerCmd(containerId).exec();
               LOG.info("`docker start {}` - Container started", containerId);
               return DockerContainer.apply(client, config, containerId);
           });
    }

    @AllArgsConstructor(staticName = "apply")
    public static class DockerContainer implements Container {

        private final DockerClient client;

        private final ContainerConfig config;

        private final String containerId;

        @Override
        public ContainerConfig getConfig() {
            return config;
        }

        @Override
        public ContainerStatus getStatus() {
            return ContainerStatus.CREATED;
        }

        @Override
        public Map<Integer, Integer> getMappedPorts() {
            return Maps.newHashMap();
        }

        @Override
        public String getLogs() {
            return "";
        }

        @Override
        public CompletionStage<Done> start() {
            return null;
        }

        @Override
        public CompletionStage<Done> stop() {
            return null;
        }

        @Override
        public CompletionStage<Done> remove() {
            return null;
        }

    }

}
