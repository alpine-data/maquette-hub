package maquette.core.entities.projects;

import lombok.AllArgsConstructor;
import maquette.core.entities.projects.exceptions.ApplicationAlreadyExistsException;
import maquette.core.entities.projects.exceptions.ApplicationNotFoundException;
import maquette.core.entities.projects.model.apps.Application;
import maquette.core.entities.projects.ports.ApplicationsRepository;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.user.User;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class ApplicationEntities {

   private final UID project;

   private final ApplicationsRepository repository;

   public CompletionStage<Application> createApplication(
      User executor, String name, String description, String gitRepository) {

      return repository.findApplicationByName(project, name)
         .thenCompose(maybeApp -> {
            if (maybeApp.isPresent()) {
               return CompletableFuture.failedFuture(ApplicationAlreadyExistsException.apply(name));
            } else {
               var created = ActionMetadata.apply(executor, Instant.now());
               var secret = UUID.randomUUID().toString();
               var app = Application.apply(UID.apply(), name, secret, description, gitRepository, created, created);

               return repository
                  .insertOrUpdateApplication(project, app)
                  .thenApply(d -> app);
            }
         });
   }

   public CompletionStage<ApplicationEntity> getApplicationById(UID id) {
      return repository
         .findApplicationById(project, id)
         .thenApply(opt -> opt.map(app -> ApplicationEntity.apply(project, app.getId(), repository)))
         .thenApply(opt -> opt.orElseThrow(() -> ApplicationNotFoundException.apply(id.getValue())));
   }

   public CompletionStage<ApplicationEntity> getApplicationByName(String name) {
      return repository
         .findApplicationByName(project, name)
         .thenApply(opt -> opt.map(app -> ApplicationEntity.apply(project, app.getId(), repository)))
         .thenApply(opt -> opt.orElseThrow(() -> ApplicationNotFoundException.apply(name)));
   }

   public CompletionStage<List<Application>> listApplications() {
      return repository
         .listApplications(project)
         .thenApply(apps -> apps
            .stream()
            .sorted(Comparator.comparing(Application::getName))
            .collect(Collectors.toList()));
   }

}
