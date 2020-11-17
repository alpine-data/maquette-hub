package maquette.core.entities.sandboxes;

import com.oblac.nomen.Nomen;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.sandboxes.model.SandboxProperties;
import maquette.core.ports.SandboxesRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class Sandboxes {

   private final SandboxesRepository repository;

   public CompletionStage<SandboxProperties> createSandbox(String projectId, String name) {
      if (Objects.isNull(name)) {
         name = Nomen.randomName();
         // TODO mw: Make sure name is not used yet.
      }

      var id = Operators.hash();
      var properties = SandboxProperties.apply(id, name);

      return repository
         .insertOrUpdateSandbox(projectId, properties)
         .thenApply(done -> properties);
   }

   public CompletionStage<Optional<Sandbox>> findSandboxByName(String projectId, String name) {
      return repository
         .findSandboxByName(projectId, name)
         .thenApply(sandbox -> sandbox.map(p -> Sandbox.apply(projectId, p.getId(), repository)));
   }

   public CompletionStage<List<SandboxProperties>> listSandboxes(String projectId) {
      return repository.listSandboxes(projectId);
   }

}
