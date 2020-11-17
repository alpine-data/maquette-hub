package maquette.core.entities.sandboxes;

import akka.Done;
import lombok.AllArgsConstructor;
import lombok.Getter;
import maquette.core.entities.sandboxes.exceptions.SandboxNotFoundException;
import maquette.core.entities.sandboxes.model.SandboxProperties;
import maquette.core.ports.SandboxesRepository;

import java.util.concurrent.CompletionStage;

@Getter
@AllArgsConstructor(staticName = "apply")
public final class Sandbox {

   private final String projectId;

   private final String id;

   private final SandboxesRepository repository;

   public CompletionStage<Done> addProcess(int processId) {
      return getProperties()
         .thenApply(p -> p.withProcess(processId))
         .thenCompose(p -> repository.insertOrUpdateSandbox(projectId, p));
   }

   public CompletionStage<Done> addDeployment(String deploymentId) {
      return getProperties()
         .thenApply(p -> p.withDeployment(deploymentId))
         .thenCompose(p -> repository.insertOrUpdateSandbox(projectId, p));
   }

   public CompletionStage<Done> removeDeployment(String deploymentId) {
      return getProperties()
         .thenApply(p -> p.removeDeployment(deploymentId))
         .thenCompose(p -> repository.insertOrUpdateSandbox(projectId, p));
   }

   public CompletionStage<SandboxProperties> getProperties() {
      return repository
         .findSandboxById(projectId, id)
         .thenApply(maybeProperties -> {
            if (maybeProperties.isPresent()) {
               return maybeProperties.get();
            } else {
               throw SandboxNotFoundException.apply(id);
            }
         });
   }

}
