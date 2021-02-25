package maquette.core.entities.projects;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.entities.projects.exceptions.ApplicationNotFoundException;
import maquette.core.entities.projects.model.apps.Application;
import maquette.core.entities.projects.ports.ApplicationsRepository;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.user.User;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class ApplicationEntity {

   UID project;

   UID id;

   ApplicationsRepository applications;

   public CompletionStage<Done> renewSecret(User executor) {
      return getProperties()
         .thenCompose(app -> {
            var updated = app
               .withSecret(UUID.randomUUID().toString())
               .withUpdated(ActionMetadata.apply(executor));

            return applications.insertOrUpdateApplication(project, updated);
         });
   }

   public UID getId() {
      return id;
   }

   public UID getProject() {
      return project;
   }

   public CompletionStage<Application> getProperties() {
      return applications
         .findApplicationById(project, id)
         .thenApply(opt -> opt.orElseThrow(() -> ApplicationNotFoundException.apply(id.getValue())));
   }

   public CompletionStage<Done> remove(User executor) {
      return applications.removeApplication(project, id);
   }

}
