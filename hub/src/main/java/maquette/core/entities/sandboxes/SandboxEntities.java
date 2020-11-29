package maquette.core.entities.sandboxes;

import com.oblac.nomen.Nomen;
import lombok.AllArgsConstructor;
import maquette.core.entities.sandboxes.exceptions.SandboxNotFoundException;
import maquette.core.entities.sandboxes.model.SandboxProperties;
import maquette.core.ports.SandboxesRepository;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.user.User;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class SandboxEntities {

   private final SandboxesRepository repository;

   public CompletionStage<SandboxProperties> createSandbox(User executor, UID project, String name) {
      if (Objects.isNull(name)) {
         name = Nomen.randomName();
         // TODO mw: Make sure name is not used yet.
      }

      var sandbox = SandboxProperties.apply(UID.apply(), name, ActionMetadata.apply(executor));

      return repository
         .insertOrUpdateSandbox(project, sandbox)
         .thenApply(done -> sandbox);
   }

   public CompletionStage<Optional<SandboxEntity>> findSandboxById(UID project, UID sandbox) {
      return repository
         .findSandboxById(project, sandbox)
         .thenApply(s -> s.map(p -> SandboxEntity.apply(project, sandbox, repository)));
   }

   public CompletionStage<Optional<SandboxEntity>> findSandboxByName(UID project, String name) {
      return repository
         .findSandboxByName(project, name)
         .thenApply(sandbox -> sandbox.map(p -> SandboxEntity.apply(project, p.getId(), repository)));
   }

   public CompletionStage<SandboxEntity> getSandboxBxId(UID project, UID sandbox) {
      return findSandboxById(project, sandbox).thenApply(opt -> opt.orElseThrow(() -> SandboxNotFoundException.apply(project)));
   }

   public CompletionStage<SandboxEntity> getSandboxByName(UID project, String name) {
      return findSandboxByName(project, name).thenApply(opt -> opt.orElseThrow(() -> SandboxNotFoundException.apply(name)));
   }

   public CompletionStage<List<SandboxProperties>> listSandboxes(UID projectId) {
      return repository.listSandboxes(projectId);
   }

}
