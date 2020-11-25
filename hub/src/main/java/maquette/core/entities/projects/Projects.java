package maquette.core.entities.projects;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.ports.ProjectsRepository;
import maquette.core.values.ActionMetadata;
import maquette.core.values.exceptions.ProjectNotFoundException;
import maquette.core.values.user.User;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class Projects {

   private final ProjectsRepository repository;

   public CompletionStage<Project> createProject(User executor, String name, String title, String summary) {
      return findProjectByName(name)
         .thenCompose(maybeProject -> {
            if (maybeProject.isPresent()) {
               throw new RuntimeException("Project already exists!");
            } else {
               var id = Operators.hash();
               var created = ActionMetadata.apply(executor, Instant.now());
               var projectSummary = ProjectProperties
                  .apply(id, name, title, summary, created, created)
                  .withSummary(summary);

               return repository
                  .insertOrUpdateProject(projectSummary)
                  .thenApply(done -> Project.apply(repository, id));
            }
         });
   }

   public CompletionStage<Optional<Project>> findProjectById(String id) {
      return repository
         .findProjectById(id)
         .thenApply(maybeProject -> maybeProject.map(project -> Project.apply(repository, project.getId())));
   }

   public CompletionStage<Optional<Project>> findProjectByName(String name) {
      return repository
         .findProjectByName(name)
         .thenApply(maybeProject -> maybeProject.map(project -> Project.apply(repository, project.getId())));
   }

   public CompletionStage<Project> getProjectById(String id) {
      return findProjectById(id).thenApply(Optional::orElseThrow);
   }

   public CompletionStage<Project> getProjectByName(String name) {
      return findProjectByName(name)
         .thenApply(opt -> opt.orElseThrow(() -> ProjectNotFoundException.applyFromName(name)));
   }

   public CompletionStage<List<ProjectProperties>> getProjects() {
      return repository.getProjects();
   }

   public CompletionStage<List<ProjectProperties>> getProjectsByMember(User user) {
      // TODO mw: Improve for more performant search.

      return repository
         .getProjects()
         .thenCompose(all -> Operators.allOf(all
            .stream()
            .map(p -> this.getProjectById(p.getId()).thenCompose(Project::getDetails))
            .collect(Collectors.toList())))
         .thenApply(all -> all
            .stream()
            .filter(p -> p
               .getAuthorizations()
               .stream()
               .anyMatch(auth -> auth.getAuthorization().isAuthorized(user)))
            .map(details -> ProjectProperties.apply(details.getId(), details.getName(), details.getTitle(), details.getSummary(), details.getCreated(), details.getModified()))
            .collect(Collectors.toList()));
   }

   public CompletionStage<Done> removeProject(String projectId) {
      return repository.removeProject(projectId);
   }

}
