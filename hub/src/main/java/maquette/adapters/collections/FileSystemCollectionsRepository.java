package maquette.adapters.collections;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.adapters.companions.DataAccessRequestsFileSystemCompanion;
import maquette.adapters.companions.FileSystemDataAssetRepository;
import maquette.adapters.companions.MembersFileSystemCompanion;
import maquette.config.FileSystemRepositoryConfiguration;
import maquette.core.entities.data.collections.model.CollectionProperties;
import maquette.core.entities.data.collections.model.CollectionTag;
import maquette.core.ports.CollectionsRepository;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequestProperties;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.data.DataAssetMemberRole;
import maquette.core.values.data.binary.BinaryObject;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileSystemCollectionsRepository implements CollectionsRepository {

   private final FileSystemDataAssetRepository<CollectionProperties> assetsCompanion;

   private final DataAccessRequestsFileSystemCompanion requestsCompanion;

   private final MembersFileSystemCompanion<DataAssetMemberRole> membersCompanion;

   private final FileSystemCollectionTagsCompanion tagsCompanion;

   private final FileSystemObjectsStore objectsStore;

   public static FileSystemCollectionsRepository apply(FileSystemRepositoryConfiguration config, ObjectMapper om) {
      var directory = config.getDirectory().resolve("shop").resolve("collections");
      var assetsCompanion = FileSystemDataAssetRepository.apply(CollectionProperties.class, directory, om);
      var requestsCompanion = DataAccessRequestsFileSystemCompanion.apply(directory, om);
      var membersCompanion = MembersFileSystemCompanion.apply(directory, om, DataAssetMemberRole.class);
      var tagsCompanion = FileSystemCollectionTagsCompanion.apply(directory, om);
      var objectsStore = FileSystemObjectsStore.apply(directory.resolve("_objects"));

      return new FileSystemCollectionsRepository(assetsCompanion, requestsCompanion, membersCompanion, tagsCompanion, objectsStore);
   }

   @Override
   public CompletionStage<List<CollectionProperties>> findAllAssets() {
      return assetsCompanion.findAllAssets();
   }

   @Override
   public CompletionStage<Optional<CollectionProperties>> findAssetById(UID asset) {
      return assetsCompanion.findAssetById(asset);
   }

   @Override
   public CompletionStage<Optional<CollectionProperties>> findAssetByName(String name) {
      return assetsCompanion.findAssetByName(name);
   }

   @Override
   public CompletionStage<Done> insertOrUpdateAsset(CollectionProperties asset) {
      return assetsCompanion.insertOrUpdateAsset(asset);
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

   @Override
   public CompletionStage<List<CollectionTag>> findAllTags(UID collection) {
      return tagsCompanion.findAllTags(collection);
   }

   @Override
   public CompletionStage<Optional<CollectionTag>> findTagByName(UID collection, String name) {
      return tagsCompanion.findTagByName(collection, name);
   }

   @Override
   public CompletionStage<Done> insertOrUpdateTag(UID collection, CollectionTag tag) {
      return tagsCompanion.insertOrUpdateTag(collection, tag);
   }

   @Override
   public CompletionStage<Done> saveObject(String key, BinaryObject binary) {
      return objectsStore.saveObject(key, binary);
   }

   @Override
   public CompletionStage<Done> deleteObject(String key) {
      return objectsStore.deleteObject(key);
   }

   @Override
   public CompletionStage<Optional<BinaryObject>> readObject(String key) {
      return objectsStore.readObject(key);
   }
}
