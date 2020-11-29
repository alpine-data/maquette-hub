package maquette.core.entities.projects;

import akka.Done;
import akka.japi.Pair;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.projects.exceptions.ProjectAlreadyExistsException;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.ports.ProjectsRepository;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.exceptions.ProjectNotFoundException;
import maquette.core.values.user.User;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class ProjectEntities {

   private final ProjectsRepository repository;

   public CompletionStage<ProjectProperties> createProject(User executor, String name, String title, String summary) {
      return findProjectByName(name)
         .thenCompose(maybeProject -> {
            if (maybeProject.isPresent()) {
               return CompletableFuture.failedFuture(ProjectAlreadyExistsException.apply(name));
            } else {
               var created = ActionMetadata.apply(executor, Instant.now());
               var properties = ProjectProperties.apply(UID.apply(), name, title, summary, created, created);

               return repository
                  .insertOrUpdateProject(properties)
                  .thenApply(done -> properties);
            }
         });
   }

   public CompletionStage<Optional<ProjectEntity>> findProjectById(UID id) {
      return repository
         .findProjectById(id)
         .thenApply(maybeProject -> maybeProject.map(project -> ProjectEntity.apply(project.getId(), repository)));
   }

   public CompletionStage<Optional<ProjectEntity>> findProjectByName(String name) {
      return repository
         .findProjectByName(name)
         .thenApply(maybeProject -> maybeProject.map(project -> ProjectEntity.apply(project.getId(), repository)));
   }

   public CompletionStage<ProjectEntity> getProjectById(UID id) {
      return findProjectById(id).thenApply(Optional::orElseThrow);
   }

   public CompletionStage<ProjectEntity> getProjectByName(String name) {
      return findProjectByName(name)
         .thenApply(opt -> opt.orElseThrow(() -> ProjectNotFoundException.applyFromName(name)));
   }

   public CompletionStage<List<ProjectProperties>> getProjects() {
      return repository.getProjects();
   }

   public CompletionStage<List<ProjectProperties>> getProjectsByMember(User user) {
      return repository
         .getProjects()
         .thenCompose(all -> Operators.allOf(all
            .stream()
            .map(p -> ProjectEntity
               .apply(p.getId(), repository)
               .members()
               .getMembers()
               .thenApply(members -> Pair.create(p, members)))))
         .thenApply(all -> all
            .stream()
            .filter(p -> {
               var members = p.second();
               return members.stream().anyMatch(granted -> granted.getAuthorization().authorizes(user));
            })
            .map(Pair::first)
            .collect(Collectors.toList()));
   }

   public CompletionStage<Done> removeProject(UID id) {
      return repository.removeProject(id);
   }

}
