package maquette.adapters;

import akka.Done;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;
import lombok.AllArgsConstructor;
import maquette.core.ports.InfrastructureProviderPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public class DockerInfrastructureProvider implements InfrastructureProviderPort {

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
    public CompletionStage<Container> createContainer(String image) {
        var callback = CompletionStageResultCallback.<PullResponseItem>apply("docker pull hello-world", LOG);
        client.pullImageCmd("hello-world").exec(callback);

        return callback
                .result()
                .thenApply(done -> client
                        .createContainerCmd("hello-world")
                        .withName("my-container-123")
                        .exec())
                .thenApply(createContainerResponse -> {
                    Arrays
                            .stream(createContainerResponse.getWarnings())
                            .map(s -> String.format("`docker create %s/%s` - %s", image, "latest", s))
                            .forEach(LOG::warn);

                    LOG.info("`docker create {}/{}` - Completed with id `{}`", image, "latest", createContainerResponse.getId());

                    return createContainerResponse.getId();
                })
                .thenApply(containerId -> {
                    client.startContainerCmd(containerId).exec();
                    LOG.info("`docker start {}` - Container started", containerId);
                    return DockerContainer.apply(client, containerId);
                });
    }

    @AllArgsConstructor(staticName = "apply")
    public static class DockerContainer implements Container {

        private final DockerClient client;

        private final String containerId;

        @Override
        public CompletionStage<Done> stop() {
            return null;
        }

    }

}
