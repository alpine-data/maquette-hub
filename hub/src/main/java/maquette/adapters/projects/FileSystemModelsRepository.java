package maquette.adapters.projects;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.adapters.companions.MembersFileSystemCompanion;
import maquette.common.Operators;
import maquette.config.FileSystemRepositoryConfiguration;
import maquette.core.entities.projects.model.model.ModelProperties;
import maquette.core.entities.projects.model.model.ModelMemberRole;
import maquette.core.entities.projects.ports.ModelsRepository;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.GrantedAuthorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileSystemModelsRepository implements ModelsRepository {

   private static final Logger LOG = LoggerFactory.getLogger(ModelsRepository.class);

   private final Path directory;

   private final ObjectMapper om;

   public static FileSystemModelsRepository apply(FileSystemRepositoryConfiguration config, ObjectMapper om) {
      var directory = config.getDirectory().resolve("projects");
      Operators.suppressExceptions(() -> Files.createDirectories(directory));
      return new FileSystemModelsRepository(directory, om);
   }

   private Path getModelsFile(UID project, String model) {
      var file =  directory
         .resolve(project.getValue())
         .resolve("models")
         .resolve(model)
         .resolve("model.json");

      Operators.suppressExceptions(() -> Files.createDirectories(file.getParent()));

      return file;
   }

   @Override
   public CompletionStage<Done> insertOrUpdateModel(UID project, ModelProperties model) {
      var file = getModelsFile(project, model.getName());
      Operators.suppressExceptions(() -> om.writeValue(file.toFile(), model));
      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<Optional<ModelProperties>> findModelByName(UID project, String name) {
      var file = getModelsFile(project, name);

      if (!Files.exists(file)) {
         return CompletableFuture.completedFuture(Optional.empty());
      }

      var result = Operators.ignoreExceptionsWithDefault(
         () -> om.readValue(file.toFile(), ModelProperties.class),
         null, LOG);

      return CompletableFuture.completedFuture(Optional.ofNullable(result));
   }

   @Override
   public CompletionStage<List<GrantedAuthorization<ModelMemberRole>>> findAllMembers(UID project, String model) {
      return getMembersCompanion(project).findAllMembers(UID.apply(model));
   }

   @Override
   public CompletionStage<List<GrantedAuthorization<ModelMemberRole>>> findMembersByRole(UID project, String model, ModelMemberRole role) {
      return getMembersCompanion(project).findMembersByRole(UID.apply(model), role);
   }

   @Override
   public CompletionStage<Done> insertOrUpdateMember(UID project, String model, GrantedAuthorization<ModelMemberRole> member) {
      return getMembersCompanion(project).insertOrUpdateMember(UID.apply(model), member);
   }

   @Override
   public CompletionStage<Done> removeMember(UID project, String model, Authorization member) {
      return getMembersCompanion(project).removeMember(UID.apply(model), member);
   }

   private MembersFileSystemCompanion<ModelMemberRole> getMembersCompanion(UID project) {
      return MembersFileSystemCompanion.apply(
         directory.resolve(project.getValue()).resolve("models"),
         om, ModelMemberRole.class);
   }

}
