package maquette.adapters.sandboxes;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.sandboxes.model.SandboxProperties;
import maquette.core.ports.SandboxesRepository;
import maquette.core.values.UID;

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

   private Path getProjectDirectory(UID project) {
      var path = config.getDirectory().resolve(project.getValue()).resolve("sandboxes");
      Operators.suppressExceptions(() -> Files.createDirectories(path));
      return path;
   }

   private Path getSandboxFile(UID project, UID sandbox) {
      return getProjectDirectory(project).resolve(sandbox.getValue() + ".sandbox.json");
   }


   @Override
   public CompletionStage<Optional<SandboxProperties>> findSandboxById(UID project, UID sandbox) {
      var file = getSandboxFile(project, sandbox);

      if (Files.exists(file)) {
         var result = Operators.suppressExceptions(() -> om.readValue(file.toFile(), SandboxProperties.class));
         return CompletableFuture.completedFuture(Optional.of(result));
      } else {
         return CompletableFuture.completedFuture(Optional.empty());
      }
   }

   @Override
   public CompletionStage<Optional<SandboxProperties>> findSandboxByName(UID project, String sandbox) {
      return listSandboxes(project).thenApply(sandboxes -> sandboxes
         .stream()
         .filter(s -> s.getName().equals(sandbox))
         .findFirst());
   }

   @Override
   public CompletionStage<Done> insertOrUpdateSandbox(UID project, SandboxProperties sandbox) {
      var file = getSandboxFile(project, sandbox.getId());
      Operators.suppressExceptions(() -> om.writeValue(file.toFile(), sandbox));
      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<List<SandboxProperties>> listSandboxes(UID project) {
      var result = Operators
         .suppressExceptions(() -> Files.list(getProjectDirectory(project))
         .map(path -> Operators.suppressExceptions(() -> om.readValue(path.toFile(), SandboxProperties.class)))
         .collect(Collectors.toList()));

      return CompletableFuture.completedFuture(result);
   }

}
