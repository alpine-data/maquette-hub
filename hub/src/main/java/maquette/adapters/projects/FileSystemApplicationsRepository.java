package maquette.adapters.projects;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.config.FileSystemRepositoryConfiguration;
import maquette.core.entities.projects.model.apps.Application;
import maquette.core.entities.projects.ports.ApplicationsRepository;
import maquette.core.values.UID;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor(staticName = "apply")
public final class FileSystemApplicationsRepository implements ApplicationsRepository {

   private final Path directory;

   private final ObjectMapper om;

   public static FileSystemApplicationsRepository apply(FileSystemRepositoryConfiguration config, ObjectMapper om) {
      var directory = config.getDirectory().resolve("projects");
      Operators.suppressExceptions(() -> Files.createDirectories(directory));
      return new FileSystemApplicationsRepository(directory, om);
   }

   private Path getFile(UID project) {
      var file = directory.resolve(project.getValue()).resolve("applications.json");
      Operators.suppressExceptions(() -> Files.createDirectories(file.getParent()));
      return file;
   }

   private List<Application> load(UID project) {
      var file = getFile(project);
      var type = om.getTypeFactory().constructCollectionType(List.class, Application.class);

      if (Files.exists(file)) {
         return Operators.suppressExceptions(() -> om.readValue(file.toFile(), type));
      } else {
         return List.of();
      }
   }

   private void save(UID project, List<Application> applications) {
      Operators.suppressExceptions(() -> om.writeValue(getFile(project).toFile(), applications));
   }

   @Override
   public CompletionStage<Done> insertOrUpdateApplication(UID project, Application app) {
      var apps = load(project)
         .stream()
         .filter(a -> !a.getId().equals(app.getId()));

      var updated = Stream
         .concat(apps, Stream.of(app))
         .collect(Collectors.toList());

      save(project, updated);

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<Optional<Application>> findApplicationByName(UID project, String name) {
      var result = load(project)
         .stream()
         .filter(app -> app.getName().equals(name))
         .findFirst();

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<Optional<Application>> findApplicationById(UID project, UID id) {
      var result = load(project)
         .stream()
         .filter(app -> app.getId().equals(id))
         .findFirst();

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<List<Application>> listApplications(UID project) {
      return CompletableFuture.completedFuture(load(project));
   }

   @Override
   public CompletionStage<Done> removeApplication(UID project, UID id) {
      var result = load(project)
         .stream()
         .filter(app -> !app.getId().equals(id))
         .collect(Collectors.toList());

      save(project, result);

      return CompletableFuture.completedFuture(Done.getInstance());
   }

}
