package maquette.adapters.infrastructure;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.config.FileSystemRepositoryConfiguration;
import maquette.core.entities.infrastructure.model.DeploymentMemento;
import maquette.core.ports.InfrastructureRepository;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileSystemInfrastructureRepository implements InfrastructureRepository {

   private final Path directory;

   private final ObjectMapper om;

   public static FileSystemInfrastructureRepository apply(
      FileSystemRepositoryConfiguration config,
      ObjectMapper om) {

      var directory = config.getDirectory().resolve("infrastructure");
      Operators.suppressExceptions(() -> Files.createDirectories(directory));
      return new FileSystemInfrastructureRepository(directory, om);
   }

   private Path getFile(String deploymentName) {
      return directory.resolve(deploymentName + ".json");
   }

   @Override
   public CompletionStage<Done> insertOrUpdateDeployment(DeploymentMemento memento) {
      var file = getFile(memento.getConfig().getName());

      Operators.suppressExceptions(() -> {
         try (OutputStream os = Files.newOutputStream(file)) {
            om.writeValue(os, memento);
         }
      });

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<Done> removeDeployment(String name) {
      var file = getFile(name);
      Operators.ignoreExceptions(() -> Files.deleteIfExists(file));

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<List<DeploymentMemento>> getDeployments() {
      var result = Operators.suppressExceptions(() -> Files
         .list(directory)
         .filter(Files::isRegularFile)
         .map(file -> Operators.ignoreExceptionsWithDefault(
            () -> Optional.of(om.readValue(file.toFile(), DeploymentMemento.class)),
            Optional.<DeploymentMemento>empty()))
         .filter(Optional::isPresent)
         .map(Optional::get)
         .collect(Collectors.toList()));

      return CompletableFuture.completedFuture(result);
   }

}
