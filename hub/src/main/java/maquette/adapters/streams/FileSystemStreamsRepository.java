package maquette.adapters.streams;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.adapters.collections.FileSystemCollectionsRepositoryConfiguration;
import maquette.adapters.companions.DataAccessRequestsFileSystemCompanion;
import maquette.adapters.companions.FileSystemDataAssetRepository;
import maquette.adapters.companions.MembersFileSystemCompanion;
import maquette.core.entities.data.streams.model.StreamProperties;
import maquette.core.ports.StreamsRepository;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequestProperties;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.data.DataAssetMemberRole;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileSystemStreamsRepository implements StreamsRepository {

   private final FileSystemDataAssetRepository<StreamProperties> assetsCompanion;

   private final DataAccessRequestsFileSystemCompanion requestsCompanion;

   private final MembersFileSystemCompanion<DataAssetMemberRole> membersCompanion;

   public static FileSystemStreamsRepository apply(FileSystemStreamsRepositoryConfiguration config, ObjectMapper om) {
      var assetsCompanion = FileSystemDataAssetRepository.apply(StreamProperties.class, config.getDirectory(), om);
      var requestsCompanion = DataAccessRequestsFileSystemCompanion.apply(config.getDirectory(), om);
      var membersCompanion = MembersFileSystemCompanion.apply(config.getDirectory(), om, DataAssetMemberRole.class);

      return new FileSystemStreamsRepository(assetsCompanion, requestsCompanion, membersCompanion);
   }

   @Override
   public CompletionStage<List<StreamProperties>> findAllAssets() {
      return assetsCompanion.findAllAssets();
   }

   @Override
   public CompletionStage<Optional<StreamProperties>> findAssetById(UID asset) {
      return assetsCompanion.findAssetById(asset);
   }

   @Override
   public CompletionStage<Optional<StreamProperties>> findAssetByName(String name) {
      return assetsCompanion.findAssetByName(name);
   }

   @Override
   public CompletionStage<Done> insertOrUpdateAsset(StreamProperties asset) {
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

}
