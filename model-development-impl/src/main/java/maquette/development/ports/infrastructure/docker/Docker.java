package maquette.development.ports.infrastructure.docker;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.databind.DefaultObjectMapperFactory;
import maquette.development.ports.infrastructure.docker.model.ContainerConfig;
import maquette.development.ports.infrastructure.docker.model.DeploymentConfig;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class Docker {

    private static final Logger LOG = LoggerFactory.getLogger(Docker.class);

    private final DockerClient client;

    private final DockerVolumeCompanion volumes;

    /**
     * This class manages deployments using local Docker instance.
     *
     * @param configuration A local (user-specific) configuration directory where current state and volume data is stored.
     * @param om An object mapper instance which is used to serialize and deserialize properties for saving state and metadata.
     * @return A new Docker instance pointing to a local docker daemon.
     */
    public static Docker apply(Path configuration, ObjectMapper om) {
        DockerClientConfig config = DefaultDockerClientConfig
            .createDefaultConfigBuilder()
            .build();

        DockerHttpClient httpClient = new ZerodepDockerHttpClient.Builder()
            .dockerHost(config.getDockerHost())
            .sslConfig(config.getSSLConfig())
            .build();

        var volumesCompanion = DockerVolumeCompanion.apply(configuration.resolve("volumes"), om);

        return apply(DockerClientImpl.getInstance(config, httpClient), volumesCompanion);
    }

    public static Docker apply() {
        var path = SystemUtils.getUserHome().toPath().resolve(".mq").resolve("docker");
        var om = DefaultObjectMapperFactory.apply().createJsonMapper(true);

        return apply(path, om);
    }

    public CompletionStage<Deployment> runDeployment(DeploymentConfig config) {
        var ops = DockerOperations.apply(client, volumes, LOG);
        var networkId = ops.createNetwork(config.getName());

        return Operators
            .allOf(config
                .getContainers()
                .stream()
                .map(cfg -> runContainer(cfg, networkId))
                .collect(Collectors.toList()))
            .thenApply(containers -> Deployment.apply(config, containers, Instant.now()));
    }

    private CompletionStage<DockerContainer> runContainer(ContainerConfig config, String networkId) {
        var ops = DockerOperations.apply(client, volumes, LOG);

        CompletionStage<Done> pulled = CompletableFuture.completedFuture(Done.getInstance());

        if (!config.getImage().startsWith("mq")) {
            pulled = ops.pullImage(config.getImage());
        }

        return pulled
            .thenApply(done -> ops.createContainer(config))
            .thenApply(containerId -> {
                ops.connectToNetwork(containerId, networkId);
                return containerId;
            })
            .thenApply(containerId -> ops.startContainer(config, containerId));
    }

}
