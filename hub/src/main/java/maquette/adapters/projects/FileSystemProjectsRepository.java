package maquette.adapters.projects;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.common.Operators;
import maquette.core.entities.project.model.ProjectSummary;
import maquette.core.ports.ProjectsRepository;
import maquette.core.values.ActionMetadata;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.GrantedAuthorization;
import org.glassfish.jersey.internal.guava.Sets;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileSystemProjectsRepository implements ProjectsRepository {

   private final FileSystemProjectsRepositoryConfiguration config;

   private final ObjectMapper om;

   @Value
   @With
   @AllArgsConstructor(staticName = "apply")
   private static class ProjectMemento {

      ProjectSummary summary;

      Set<GrantedAuthorization> authorizations;

   }

   public static FileSystemProjectsRepository apply(FileSystemProjectsRepositoryConfiguration config, ObjectMapper om) {
      Operators.suppressExceptions(() -> {
         Files.createDirectories(config.getDirectory());
      });

      return new FileSystemProjectsRepository(config, om);
   }

   private Path getProjectFile(String projectId) {
      return config.getDirectory().resolve(projectId + ".json");
   }

   private Optional<ProjectMemento> loadProject(String projectId) {
      var file = getProjectFile(projectId);

      if (Files.exists(file) && Files.isRegularFile(file)) {
         return Optional.of(Operators.suppressExceptions(() -> om.readValue(file.toFile(), ProjectMemento.class)));
      } else {
         return Optional.empty();
      }
   }

   private void saveProject(ProjectMemento memento) {
      var file = getProjectFile(memento.getSummary().getId());
      Operators.suppressExceptions(() -> om.writeValue(file.toFile(), memento));
   }

   @Override
   public CompletionStage<Done> addGrantedAuthorization(String projectId, GrantedAuthorization authorization) {
      loadProject(projectId)
         .ifPresent(project -> {
            project.authorizations.add(authorization);
            saveProject(project);
         });

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<List<GrantedAuthorization>> getGrantedAuthorizations(String projectId) {
      var result = loadProject(projectId)
         .map(memento -> List.copyOf(memento.getAuthorizations()))
         .orElse(List.of());

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<Done> removeGrantedAuthorization(String projectId, Authorization authorization) {
      loadProject(projectId)
         .ifPresent(project -> {
            var newProject = project.withAuthorizations(project
               .authorizations
               .stream()
               .filter(a -> !a.getAuthorization().equals(authorization)).collect(Collectors.toSet()));

            saveProject(newProject);
         });

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<Optional<ProjectSummary>> findProjectById(String id) {
      return CompletableFuture.completedFuture(loadProject(id).map(ProjectMemento::getSummary));
   }

   @Override
   public CompletionStage<Optional<ProjectSummary>> findProjectByName(String name) {
      return getProjects()
         .thenApply(projects -> projects
            .stream()
            .filter(p -> p.getName().equals(name))
            .findFirst());
   }

   @Override
   public CompletionStage<ProjectSummary> getProjectById(String id) {
      return findProjectById(id).thenApply(Optional::get);
   }

   @Override
   public CompletionStage<Done> insertOrUpdateProject(ProjectSummary project) {
      var memento = loadProject(project.getId())
         .map(existing -> existing.withSummary(project))
         .orElse(ProjectMemento.apply(project, Sets.newHashSet()));

      saveProject(memento);

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<List<ProjectSummary>> getProjects() {
      var result = Operators.suppressExceptions(() -> Files
         .list(config.getDirectory())
         .filter(Files::isRegularFile)
         .map(file -> Operators.ignoreExceptionsWithDefault(
            () -> Optional.of(om.readValue(file.toFile(), ProjectMemento.class)),
            Optional.<ProjectMemento>empty()))
         .filter(Optional::isPresent)
         .map(Optional::get)
         .map(ProjectMemento::getSummary)
         .collect(Collectors.toList()));

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<Done> updateLastModified(String projectId, ActionMetadata modified) {
      loadProject(projectId)
         .ifPresent(project -> {
            var updatedProject = project.withSummary(project.getSummary().withModified(modified));
            saveProject(updatedProject);
         });

      return CompletableFuture.completedFuture(Done.getInstance());
   }

}
