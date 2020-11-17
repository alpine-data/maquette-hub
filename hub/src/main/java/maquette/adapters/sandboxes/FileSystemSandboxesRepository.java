package maquette.adapters.sandboxes;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.sandboxes.model.SandboxProperties;
import maquette.core.ports.SandboxesRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public class FileSystemSandboxesRepository implements SandboxesRepository {

   private final FileSystemSandboxesRepositoryConfiguration config;

   private final ObjectMapper om;

   /*
    * Helper functions
    */
   private Path getProjectDirectory(String projectId) {
      var path = config.getDirectory().resolve(projectId);
      Operators.suppressExceptions(() -> Files.createDirectories(path));
      return path;
   }

   private Path getSandboxFile(String projectId, String sandboxId) {
      return getProjectDirectory(projectId).resolve(sandboxId + ".json");
   }


   @Override
   public CompletionStage<Optional<SandboxProperties>> findSandboxById(String projectId, String sandboxId) {
      var file = getSandboxFile(projectId, sandboxId);

      if (Files.exists(file)) {
         var result = Operators.suppressExceptions(() -> om.readValue(file.toFile(), SandboxProperties.class));
         return CompletableFuture.completedFuture(Optional.of(result));
      } else {
         return CompletableFuture.completedFuture(Optional.empty());
      }
   }

   @Override
   public CompletionStage<Optional<SandboxProperties>> findSandboxByName(String projectId, String sandboxName) {
      return listSandboxes(projectId).thenApply(sandboxes -> sandboxes
         .stream()
         .filter(s -> s.getName().equals(sandboxName))
         .findFirst());
   }

   @Override
   public CompletionStage<Done> insertOrUpdateSandbox(String projectId, SandboxProperties sandbox) {
      var file = getSandboxFile(projectId, sandbox.getId());
      Operators.suppressExceptions(() -> om.writeValue(file.toFile(), sandbox));
      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<List<SandboxProperties>> listSandboxes(String projectId) {
      var result = Operators.suppressExceptions(() -> Files
         .list(getProjectDirectory(projectId))
         .map(path -> Operators.suppressExceptions(() -> om.readValue(path.toFile(), SandboxProperties.class)))
         .collect(Collectors.toList()));

      return CompletableFuture.completedFuture(result);
   }
}
