package maquette.adapters.infrastructure;

import akka.Done;
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
import maquette.common.DockerOperations;
import maquette.common.Operators;
import maquette.core.entities.infrastructure.Container;
import maquette.core.entities.infrastructure.model.ContainerConfig;
import maquette.core.entities.infrastructure.model.ContainerStatus;
import maquette.core.ports.InfrastructureProvider;
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
   public CompletionStage<Container> runContainer(ContainerConfig config) {
      var ops = DockerOperations.apply(client, LOG);

      return ops.pullImage(config.getImage())
         .thenApply(done -> ops.createContainer(config))
         .thenApply(containerId -> ops.startContainer(config, containerId));
   }

   @AllArgsConstructor(staticName = "apply")
   public static class DockerContainer implements Container {

      private final Logger LOG = LoggerFactory.getLogger(DockerContainer.class);

      private final DockerClient client;

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
      public ContainerStatus getStatus() {
         return getContainer()
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
      }

      @Override
      public Map<Integer, URL> getMappedPortUrls() {
         return getContainer()
            .map(container -> Arrays
               .stream(container.ports)
               .collect(Collectors.toMap(
                  ContainerPort::getPrivatePort,
                  p -> Operators.suppressExceptions(() -> new URL("http://localhost:" + p.getPublicPort())))))
            .orElse(Maps.newHashMap());
      }

      @Override
      public String getLogs() {
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

         return "";
      }

      @Override
      public CompletionStage<Done> start() {
         DockerOperations
            .apply(client, LOG)
            .startContainer(config, containerId);

         return CompletableFuture.completedFuture(Done.getInstance());
      }

      @Override
      public CompletionStage<Done> stop() {
         DockerOperations
            .apply(client, LOG)
            .stopContainer(containerId);

         return CompletableFuture.completedFuture(Done.getInstance());
      }

      @Override
      public CompletionStage<Done> remove() {
         DockerOperations
            .apply(client, LOG)
            .removeContainer(containerId);

         return CompletableFuture.completedFuture(Done.getInstance());
      }

   }

}
