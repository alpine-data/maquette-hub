package maquette.adapters.infrastructure;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.ContainerPort;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.infrastructure.Container;
import maquette.core.entities.infrastructure.Deployment;
import maquette.core.entities.infrastructure.model.*;
import maquette.core.entities.infrastructure.ports.InfrastructureProvider;
import maquette.core.values.UID;
import maquette.core.values.data.binary.CompressedBinaryObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.net.URL;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class DockerInfrastructureProvider implements InfrastructureProvider {

   private static final Logger LOG = LoggerFactory.getLogger(DockerInfrastructureProvider.class);

   private final DockerClient client;

   private final DockerVolumeCompanion volumes;

   public static DockerInfrastructureProvider apply(Path volumes, ObjectMapper om) {
      DockerClientConfig config = DefaultDockerClientConfig
         .createDefaultConfigBuilder()
         .build();

      DockerHttpClient httpClient = new ZerodepDockerHttpClient.Builder()
         .dockerHost(config.getDockerHost())
         .sslConfig(config.getSSLConfig())
         .build();

      var volumesCompanion = DockerVolumeCompanion.apply(volumes, om);

      return apply(DockerClientImpl.getInstance(config, httpClient), volumesCompanion);
   }

   @Override
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

   @Override
   public CompletionStage<Container> runContainer(ContainerConfig config) {
      return runContainer(config, null);
   }

   /*
    * Volumes
    */

   @Override
   public CompletionStage<Done> createVolume(DataVolume volume) {
      return volumes.createVolume(volume);
   }

   @Override
   public CompletionStage<Done> createVolume(DataVolume volume, CompressedBinaryObject initialData) {
      return volumes.createVolume(volume, initialData);
   }

   @Override
   public CompletionStage<List<DataVolume>> getVolumes() {
      return volumes.getVolumes();
   }

   @Override
   public CompletionStage<Optional<DataVolume>> findVolumeById(UID volume) {
      return volumes.findVolumeById(volume);
   }

   @Override
   public CompletionStage<Done> removeVolume(UID volume) {
      return volumes.removeVolume(volume);
   }

   private CompletionStage<Container> runContainer(ContainerConfig config, String networkId) {
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

   @AllArgsConstructor(staticName = "apply")
   public static class DockerContainer implements Container {

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
            .filter(container -> container.getId().equals(containerId))
            .findFirst();
      }

      @Override
      public ContainerConfig getConfig() {
         return config;
      }

      @Override
      public CompletionStage<ContainerStatus> getStatus() {
         var result = getContainer()
            .map(container -> {
               var status = container.getStatus().toLowerCase();

               if (status.contains("up")) {
                  return ContainerStatus.RUNNING;
               } else if (status.contains("exited (0)")) {
                  return ContainerStatus.STOPPED;
               } else if (status.contains("exited")) {
                  return ContainerStatus.FAILED;
               } else {
                  return ContainerStatus.CREATED;
               }
            }).orElse(ContainerStatus.STOPPED);

         return CompletableFuture.completedFuture(result);
      }

      @Override
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

      @Override
      public CompletionStage<String> getLogs() {
         client.logContainerCmd(containerId).exec(new ResultCallback<Frame>() {
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

      @Override
      public CompletionStage<Done> start() {
         DockerOperations
            .apply(client, volumes, LOG)
            .startContainer(config, containerId);

         return CompletableFuture.completedFuture(Done.getInstance());
      }

      @Override
      public CompletionStage<Done> stop() {
         DockerOperations
            .apply(client, volumes, LOG)
            .stopContainer(containerId);

         return CompletableFuture.completedFuture(Done.getInstance());
      }

      @Override
      public CompletionStage<Done> remove() {
         DockerOperations
            .apply(client, volumes, LOG)
            .removeContainer(containerId);

         return CompletableFuture.completedFuture(Done.getInstance());
      }

      @Override
      public CompletionStage<ContainerProperties> getProperties() {
         return Operators.compose(getStatus(), getMappedPortUrls(), (status, urls) ->
            ContainerProperties.apply(config, status, urls));
      }

   }

}
