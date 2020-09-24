package maquette.common;

import akka.Done;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.PullResponseItem;
import lombok.AllArgsConstructor;
import maquette.adapters.infrastructure.DockerInfrastructureProvider;
import maquette.core.entities.infrastructure.model.ContainerConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class DockerOperations {

   DockerClient client;

   Logger log;

   public String createContainer(ContainerConfig config) {
      return client
         .listContainersCmd()
         .withShowAll(true)
         .exec()
         .stream()
         .filter(container -> Arrays.asList(container.getNames()).contains("/" + config.getName()))
         .findFirst()
         .map(com.github.dockerjava.api.model.Container::getId)
         .orElseGet(() -> {
            var exposedPorts = config
               .getPorts()
               .stream()
               .map(spec -> Pair.of(spec, ExposedPort.tcp(spec.getContainerPort())))
               .collect(Collectors.toList());

            Ports portBindings = new Ports();
            exposedPorts.forEach(p -> {
               if (p.getLeft().getHostPort().isPresent()) {
                  portBindings.bind(p.getRight(), Ports.Binding.bindIpAndPort("0.0.0.0", p.getLeft().getHostPort().get()));
               } else {
                  portBindings.bind(p.getRight(), Ports.Binding.bindIp("0.0.0.0"));
               }
            });

            var environment = config
               .getEnvironment()
               .entrySet()
               .stream()
               .map(e -> e.getKey() + "=" + e.getValue())
               .collect(Collectors.toList());

            var createCmd = client
               .createContainerCmd(config.getImage())
               .withName(config.getName())
               .withEnv(environment)
               .withExposedPorts(exposedPorts.stream().map(Pair::getRight).collect(Collectors.toList()))
               .withHostConfig(new HostConfig().withPortBindings(portBindings));

            config.getCommand().ifPresent(cmd -> createCmd.withCmd(cmd.split(" ")));

            var response = createCmd.exec();

            Arrays
               .stream(response.getWarnings())
               .map(s -> String.format("`docker create %s` - %s", config.getImage(), s))
               .forEach(log::warn);

            log.info("`docker create {}` - Completed with id `{}`", config.getImage(), response.getId());

            return response.getId();
         });
   }

   public CompletionStage<Done> pullImage(String image) {
      var exists = client
         .listImagesCmd()
         .exec()
         .stream()
         .anyMatch(i -> Arrays.asList(i.getRepoTags()).contains(image));

      if (!exists) {
         var callback = CompletionStageResultCallback.<PullResponseItem>apply(
            String.format("docker pull %s", image), log);
         client.pullImageCmd(image).exec(callback);
         return callback.result();
      } else {
         return CompletableFuture.completedFuture(Done.getInstance());
      }
   }

   public DockerInfrastructureProvider.DockerContainer startContainer(ContainerConfig config, String containerId) {
      return client
         .listContainersCmd()
         .exec()
         .stream()
         .filter(container -> container.getId().equals(containerId))
         .findFirst()
         .map(container -> DockerInfrastructureProvider.DockerContainer.apply(client, config, containerId))
         .orElseGet(() -> {
            client.startContainerCmd(containerId).exec();
            log.info("`docker start {}` - Container started", containerId);

            System.out.println(client.inspectContainerCmd(containerId).exec().getHostConfig());

            return DockerInfrastructureProvider.DockerContainer.apply(client, config, containerId);
         });
   }

   public void stopContainer(String containerId) {
      client
         .stopContainerCmd(containerId)
         .exec();
   }

   public void removeContainer(String containerId) {
      client
         .removeContainerCmd(containerId)
         .withForce(true)
         .exec();
   }

}
