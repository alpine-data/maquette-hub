package maquette.adapters.datasets;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.adapters.companions.DataAccessRequestsFileSystemCompanion;
import maquette.adapters.companions.DatasetRevisionsFileSystemCompanion;
import maquette.adapters.companions.FileSystemDataAssetRepository;
import maquette.adapters.companions.MembersFileSystemCompanion;
import maquette.common.Operators;
import maquette.core.entities.data.datasets.model.DatasetProperties;
import maquette.core.entities.data.datasets.model.DatasetVersion;
import maquette.core.entities.data.datasets.model.revisions.CommittedRevision;
import maquette.core.entities.data.datasets.model.revisions.Revision;
import maquette.core.ports.DatasetsRepository;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequestProperties;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.data.DataAssetMemberRole;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FileSystemDatasetsRepository implements DatasetsRepository {

   private final FileSystemDataAssetRepository<DatasetProperties> assetsCompanion;

   private final DataAccessRequestsFileSystemCompanion requestsCompanion;

   private final MembersFileSystemCompanion<DataAssetMemberRole> membersCompanion;

   private final DatasetRevisionsFileSystemCompanion revisionsCompanion;

   public static FileSystemDatasetsRepository apply(FileSystemDatasetsRepositoryConfiguration config, ObjectMapper om) {
      var assetsCompanion = FileSystemDataAssetRepository.apply(DatasetProperties.class, config.getDirectory(), om);
      var requestsCompanion = DataAccessRequestsFileSystemCompanion.apply(config.getDirectory(), om);
      var membersCompanion = MembersFileSystemCompanion.apply(config.getDirectory(), om, DataAssetMemberRole.class);
      var revisionsCompanion = DatasetRevisionsFileSystemCompanion.apply(config.getDirectory(), om);

      return new FileSystemDatasetsRepository(assetsCompanion, requestsCompanion, membersCompanion, revisionsCompanion);
   }

   @Override
   public CompletionStage<List<DatasetProperties>> findAllAssets() {
      return assetsCompanion.findAllAssets();
   }

   @Override
   public CompletionStage<List<Revision>> findAllRevisions(UID dataset) {
      return revisionsCompanion.findAllRevisions(dataset);
   }

   @Override
   public CompletionStage<List<CommittedRevision>> findAllVersions(UID dataset) {
      return revisionsCompanion.findAllVersions(dataset);
   }

   @Override
   public CompletionStage<Optional<DatasetProperties>> findAssetById(UID asset) {
      return assetsCompanion.findAssetById(asset);
   }

   @Override
   public CompletionStage<Optional<DatasetProperties>> findAssetByName(String name) {
      return assetsCompanion.findAssetByName(name);
   }

   @Override
   public CompletionStage<Optional<Revision>> findRevisionById(UID dataset, UID revision) {
      return revisionsCompanion.findRevisionById(dataset, revision);
   }

   @Override
   public CompletionStage<Optional<CommittedRevision>> findRevisionByVersion(UID dataset, DatasetVersion version) {
      return revisionsCompanion.findRevisionByVersion(dataset, version);
   }

   @Override
   public CompletionStage<Done> insertOrUpdateAsset(DatasetProperties asset) {
      return assetsCompanion.insertOrUpdateAsset(asset);
   }

   @Override
   public CompletionStage<Done> insertOrUpdateRevision(UID dataset, Revision revision) {
      return revisionsCompanion.insertOrUpdateRevision(dataset, revision);
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
