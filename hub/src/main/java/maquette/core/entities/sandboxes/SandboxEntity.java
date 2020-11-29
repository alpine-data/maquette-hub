package maquette.core.entities.sandboxes;

import akka.Done;
import lombok.AllArgsConstructor;
import lombok.Getter;
import maquette.core.entities.sandboxes.exceptions.SandboxNotFoundException;
import maquette.core.entities.sandboxes.model.stacks.DeployedStackProperties;
import maquette.core.entities.sandboxes.model.SandboxProperties;
import maquette.core.ports.SandboxesRepository;
import maquette.core.values.UID;

import java.util.concurrent.CompletionStage;

@Getter
@AllArgsConstructor(staticName = "apply")
public final class SandboxEntity {

   private final UID project;

   private final UID id;

   private final SandboxesRepository repository;

   public CompletionStage<Done> addProcess(int processId) {
      return getProperties()
         .thenApply(p -> p.withProcess(processId))
         .thenCompose(p -> repository.insertOrUpdateSandbox(project, p));
   }

   public CompletionStage<Done> addDeployment(DeployedStackProperties deployment) {
      return getProperties()
         .thenApply(p -> p.withDeployment(deployment))
         .thenCompose(p -> repository.insertOrUpdateSandbox(project, p));
   }

   public CompletionStage<Done> removeDeployment(String deploymentId) {
      return getProperties()
         .thenApply(p -> p.removeDeployment(deploymentId))
         .thenCompose(p -> repository.insertOrUpdateSandbox(project, p));
   }

   public CompletionStage<SandboxProperties> getProperties() {
      return repository
         .findSandboxById(project, id)
         .thenApply(maybeProperties -> {
            if (maybeProperties.isPresent()) {
               return maybeProperties.get();
            } else {
               throw SandboxNotFoundException.apply(id);
            }
         });
   }

}
