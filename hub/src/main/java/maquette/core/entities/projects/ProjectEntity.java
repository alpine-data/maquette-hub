package maquette.core.entities.projects;

import akka.Done;
import lombok.AllArgsConstructor;
import lombok.Getter;
import maquette.core.entities.companions.MembersCompanion;
import maquette.core.entities.projects.model.MlflowConfiguration;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.entities.projects.model.ProjectMemberRole;
import maquette.core.entities.projects.ports.ModelsRepository;
import maquette.core.ports.ProjectsRepository;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.exceptions.ProjectNotFoundException;
import maquette.core.values.user.User;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@Getter
@AllArgsConstructor(staticName = "apply")
public final class ProjectEntity {

   private final UID id;

   private final ProjectsRepository repository;

   private final ModelsRepository models;

   public MembersCompanion<ProjectMemberRole> members() {
      return MembersCompanion.apply(id, repository);
   }

   public CompletionStage<Boolean> isMember(User user) {
      return isMember(user, null);
   }

   public CompletionStage<Boolean> isMember(User user, ProjectMemberRole role) {
      return members()
         .getMembers()
         .thenApply(members -> members
            .stream()
            .anyMatch(granted -> granted.getAuthorization().authorizes(user) && (Objects.isNull(role) || granted.getRole().equals(role))));
   }

   public CompletionStage<Done> updateProperties(User executor, String name, String title, String summary) {
      // TODO mw: value validation ...

      return getProperties()
         .thenCompose(properties -> {
            var updated = properties
               .withName(name)
               .withTitle(title)
               .withSummary(summary)
               .withModified(ActionMetadata.apply(executor));

            return repository.insertOrUpdateProject(updated);
         });
   }

   public CompletionStage<Done> setMlflowConfiguration(MlflowConfiguration config) {
      return getProperties().thenCompose(properties -> {
         var updated = properties.withMlflowConfiguration(config);
         return repository.insertOrUpdateProject(updated);
      });
   }

   public CompletionStage<ProjectProperties> getProperties() {
      return repository
         .findProjectById(id)
         .thenApply(opt -> opt.orElseThrow(() -> ProjectNotFoundException.applyFromId(id)));
   }

   public CompletionStage<ModelEntities> getModels() {
      return getProperties()
         .thenApply(ProjectProperties::getMlflowConfiguration)
         .thenApply(Optional::orElseThrow)
         .thenApply(configuration -> ModelEntities.apply(id, configuration, models));
   }

}
