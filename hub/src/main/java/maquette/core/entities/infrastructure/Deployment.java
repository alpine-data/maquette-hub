package maquette.core.entities.infrastructure;

import akka.Done;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import maquette.common.Operators;
import maquette.core.entities.infrastructure.model.DeploymentConfig;
import maquette.core.entities.infrastructure.model.DeploymentMemento;
import maquette.core.entities.infrastructure.model.DeploymentProperties;
import maquette.core.entities.infrastructure.model.DeploymentStatus;

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

   private final List<Container> containers;

   private final Instant created;

   private DeploymentStatus status;

   public static Deployment apply(DeploymentConfig config, List<Container> containers, Instant created) {
      return new Deployment(config, List.copyOf(containers), Instant.now(), DeploymentStatus.STARTED);
   }

   public Optional<Container> findContainer(String name) {
      return containers.stream().filter(c -> c.getConfig().getName().equals(name)).findFirst();
   }

   public Container getContainer(String name) {
      return findContainer(name).orElseThrow(() -> new RuntimeException("Container not found ..."));
   }

   public CompletionStage<DeploymentProperties> getProperties() {
      return Operators
         .allOf(containers
            .stream()
            .map(Container::getProperties)
            .collect(Collectors.toList()))
      .thenApply(containerProperties -> DeploymentProperties.apply(config, containerProperties, created, status));
   }

   public CompletionStage<Done> stop() {
      return Operators
         .allOf(containers.stream().map(Container::stop).collect(Collectors.toList()))
         .thenApply(done -> {
            status = DeploymentStatus.STOPPED;
            return Done.getInstance();
         });
   }

   public CompletionStage<Done> start() {
      return Operators
         .allOf(containers.stream().map(Container::start).collect(Collectors.toList()))
         .thenApply(done -> {
            status = DeploymentStatus.STARTED;
            return Done.getInstance();
         });
   }

   public DeploymentMemento toMemento() {
      return DeploymentMemento.apply(config, created, status);
   }

}
