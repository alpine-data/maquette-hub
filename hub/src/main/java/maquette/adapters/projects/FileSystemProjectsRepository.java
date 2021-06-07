package maquette.adapters.projects;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.adapters.companions.MembersFileSystemCompanion;
import maquette.common.Operators;
import maquette.config.FileSystemRepositoryConfiguration;
import maquette.core.entities.projects.model.ProjectMemberRole;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.ports.ProjectsRepository;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.GrantedAuthorization;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileSystemProjectsRepository implements ProjectsRepository {

   private static final String PROPERTIES_FILE = "project.json";

   private final Path directory;

   private final ObjectMapper om;

   private final MembersFileSystemCompanion<ProjectMemberRole> membersCompanion;

   public static FileSystemProjectsRepository apply(FileSystemRepositoryConfiguration config, ObjectMapper om) {
      var directory = config.getDirectory().resolve("projects");
      var membersCompanion = MembersFileSystemCompanion.apply(directory, om, ProjectMemberRole.class);
      Operators.suppressExceptions(() -> Files.createDirectories(directory));
      return new FileSystemProjectsRepository(directory, om, membersCompanion);
   }

   private Path getProjectFile(UID id) {
      return directory.resolve(id.getValue()).resolve(PROPERTIES_FILE);
   }

   @Override
   public CompletionStage<Optional<ProjectProperties>> findProjectById(UID id) {
      var file = getProjectFile(id);

      if (Files.exists(file)) {
         var result = Operators.suppressExceptions(() -> om.readValue(file.toFile(), ProjectProperties.class));
         return CompletableFuture.completedFuture(Optional.of(result));
      } else {
         return CompletableFuture.completedFuture(Optional.empty());
      }
   }

   @Override
   public CompletionStage<Optional<ProjectProperties>> findProjectByName(String name) {
      return getProjects()
         .thenApply(all -> all
         .stream()
         .filter(p -> p.getName().equals(name))
         .findFirst());
   }

   @Override
   public CompletionStage<Done> insertOrUpdateProject(ProjectProperties project) {
      var file = getProjectFile(project.getId());
      Operators.suppressExceptions(() -> Files.createDirectories(file.getParent()));
      Operators.suppressExceptions(() -> om.writeValue(file.toFile(), project));
      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<List<ProjectProperties>> getProjects() {
      var result = Operators
         .suppressExceptions(() -> Files.list(directory))
         .filter(Files::isDirectory)
         .map(directory -> directory.resolve(PROPERTIES_FILE))
         .filter(Files::exists)
         .map(file -> Operators.suppressExceptions(() -> om.readValue(file.toFile(), ProjectProperties.class)))
         .collect(Collectors.toList());

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<Done> removeProject(UID project) {
      var file = getProjectFile(project);
      Operators.suppressExceptions(() -> Files.deleteIfExists(file));
      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<List<GrantedAuthorization<ProjectMemberRole>>> findAllMembers(UID parent) {
      return membersCompanion.findAllMembers(parent);
   }

   @Override
   public CompletionStage<List<GrantedAuthorization<ProjectMemberRole>>> findMembersByRole(UID parent, ProjectMemberRole role) {
      return membersCompanion.findMembersByRole(parent, role);
   }

   @Override
   public CompletionStage<Done> insertOrUpdateMember(UID parent, GrantedAuthorization<ProjectMemberRole> member) {
      return membersCompanion.insertOrUpdateMember(parent, member);
   }

   @Override
   public CompletionStage<Done> removeMember(UID parent, Authorization member) {
      return membersCompanion.removeMember(parent, member);
   }
}
