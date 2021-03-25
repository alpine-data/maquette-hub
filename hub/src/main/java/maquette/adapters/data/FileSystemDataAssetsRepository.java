package maquette.adapters.data;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.adapters.companions.DataAccessRequestsFileSystemCompanion;
import maquette.adapters.companions.MembersFileSystemCompanion;
import maquette.common.Operators;
import maquette.config.FileSystemRepositoryConfiguration;
import maquette.core.entities.data.model.DataAssetProperties;
import maquette.core.entities.data.ports.DataAssetsRepository;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequestProperties;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.data.DataAssetMemberRole;
import org.apache.commons.io.FileUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileSystemDataAssetsRepository implements DataAssetsRepository {

   private static final String PROPERTIES_FILE = "asset.json";

   private static final String CUSTOM_PROPERTIES_FILE = "properties.json";

   private final Path directory;

   private final ObjectMapper om;

   private final MembersFileSystemCompanion<DataAssetMemberRole> membersCompanion;

   private final DataAccessRequestsFileSystemCompanion requestsCompanion;

   public static FileSystemDataAssetsRepository apply(FileSystemRepositoryConfiguration config, ObjectMapper om) {
      var directory = config.getDirectory().resolve("shop");
      var requestsCompanion = DataAccessRequestsFileSystemCompanion.apply(directory, om);
      var membersCompanion = MembersFileSystemCompanion.apply(directory, om, DataAssetMemberRole.class);

      Operators.suppressExceptions(() -> Files.createDirectories(directory));

      return new FileSystemDataAssetsRepository(directory, om, membersCompanion, requestsCompanion);
   }

   private Path getAssetDirectory(UID dataset) {
      var dir = directory.resolve(dataset.getValue());
      Operators.suppressExceptions(() -> Files.createDirectories(dir));
      return dir;
   }

   @Override
   public CompletionStage<Optional<DataAssetProperties>> findEntityByName(String name) {
      return listEntities()
         .thenApply(datasets -> datasets
            .stream()
            .filter(d -> d.getMetadata().getName().equals(name))
            .findFirst());
   }

   @Override
   public CompletionStage<Optional<DataAssetProperties>> findEntitiesById(UID id) {
      var file = getAssetDirectory(id).resolve(PROPERTIES_FILE);

      if (Files.exists(file)) {
         var result = Operators.suppressExceptions(() -> om.readValue(file.toFile(), DataAssetProperties.class));
         return CompletableFuture.completedFuture(Optional.of(result));
      } else {
         return CompletableFuture.completedFuture(Optional.empty());
      }
   }

   @Override
   public <T> CompletionStage<Optional<T>> fetchCustomProperties(UID id, Class<T> expectedType) {
      var file = getAssetDirectory(id).resolve(CUSTOM_PROPERTIES_FILE);

      if (Files.exists(file)) {
         var result = Operators.suppressExceptions(() -> om.readValue(file.toFile(), expectedType));
         return CompletableFuture.completedFuture(Optional.of(result));
      } else {
         return CompletableFuture.completedFuture(Optional.empty());
      }
   }

   @Override
   public CompletionStage<Done> insertOrUpdateEntity(DataAssetProperties updated) {
      var file = getAssetDirectory(updated.getId()).resolve(PROPERTIES_FILE);
      Operators.suppressExceptions(() -> om.writeValue(file.toFile(), updated));
      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<Done> insertOrUpdateCustomProperties(UID id, Object customProperties) {
      if (customProperties != null) {
         var file = getAssetDirectory(id).resolve(CUSTOM_PROPERTIES_FILE);
         Operators.suppressExceptions(() -> om.writeValue(file.toFile(), customProperties));
      }

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<List<DataAssetProperties>> listEntities() {
      var result = Operators
         .suppressExceptions(() -> Files.list(directory))
         .filter(Files::isDirectory)
         .map(directory -> directory.resolve(PROPERTIES_FILE))
         .filter(Files::exists)
         .map(file -> Operators.suppressExceptions(() -> om.readValue(file.toFile(), DataAssetProperties.class)))
         .collect(Collectors.toList());

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<Done> removeEntityById(UID id) {
      var file = getAssetDirectory(id);
      Operators.ignoreExceptions(() -> FileUtils.deleteDirectory(file.toFile()));
      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<Optional<DataAccessRequestProperties>> findDataAccessRequestById(UID asset, UID request) {
      return requestsCompanion.findDataAccessRequestById(asset, request);
   }

   @Override
   public CompletionStage<Done> insertOrUpdateDataAccessRequest(DataAccessRequestProperties request) {
      return requestsCompanion.insertOrUpdateDataAccessRequest(request);
   }

   @Override
   public CompletionStage<List<DataAccessRequestProperties>> findDataAccessRequestsByProject(UID project) {
      return requestsCompanion.findDataAccessRequestsByProject(project);
   }

   @Override
   public CompletionStage<List<DataAccessRequestProperties>> findDataAccessRequestsByAsset(UID asset) {
      return requestsCompanion.findDataAccessRequestsByAsset(asset);
   }

   @Override
   public CompletionStage<Done> removeDataAccessRequest(UID asset, UID id) {
      return requestsCompanion.removeDataAccessRequest(asset, id);
   }

   @Override
   public CompletionStage<List<GrantedAuthorization<DataAssetMemberRole>>> findAllMembers(UID parent) {
      return membersCompanion.findAllMembers(parent);
   }

   @Override
   public CompletionStage<List<GrantedAuthorization<DataAssetMemberRole>>> findMembersByRole(UID parent, DataAssetMemberRole role) {
      return membersCompanion.findMembersByRole(parent, role);
   }

   @Override
   public CompletionStage<Done> insertOrUpdateMember(UID parent, GrantedAuthorization<DataAssetMemberRole> member) {
      return membersCompanion.insertOrUpdateMember(parent, member);
   }

   @Override
   public CompletionStage<Done> removeMember(UID parent, Authorization member) {
      return membersCompanion.removeMember(parent, member);
   }

}
