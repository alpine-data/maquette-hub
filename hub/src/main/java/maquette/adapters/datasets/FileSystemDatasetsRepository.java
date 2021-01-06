package maquette.adapters.datasets;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.adapters.companions.*;
import maquette.config.FileSystemRepositoryConfiguration;
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
import maquette.core.values.data.logs.DataAccessLogEntryProperties;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileSystemDatasetsRepository implements DatasetsRepository {


   private final FileSystemDataAssetRepository<DatasetProperties> assetsCompanion;

   private final DataAccessRequestsFileSystemCompanion requestsCompanion;

   private final MembersFileSystemCompanion<DataAssetMemberRole> membersCompanion;

   private final DatasetRevisionsFileSystemCompanion revisionsCompanion;

   private final AccessLogsFileSystemCompanion logsCompanion;

   public static FileSystemDatasetsRepository apply(FileSystemRepositoryConfiguration config, ObjectMapper om) {
      var directory = config.getDirectory().resolve("shop").resolve("datasets");
      var assetsCompanion = FileSystemDataAssetRepository.apply(DatasetProperties.class, directory, om);
      var requestsCompanion = DataAccessRequestsFileSystemCompanion.apply(directory, om);
      var membersCompanion = MembersFileSystemCompanion.apply(directory, om, DataAssetMemberRole.class);
      var revisionsCompanion = DatasetRevisionsFileSystemCompanion.apply(directory, om);
      var logsCompanion = AccessLogsFileSystemCompanion.apply(directory, om);

      return new FileSystemDatasetsRepository(assetsCompanion, requestsCompanion, membersCompanion, revisionsCompanion, logsCompanion);
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

   @Override
   public CompletionStage<Done> appendAccessLogEntry(DataAccessLogEntryProperties entry) {
      return logsCompanion.appendAccessLogEntry(entry);
   }

   @Override
   public CompletionStage<List<DataAccessLogEntryProperties>> findAccessLogsByAsset(UID asset) {
      return logsCompanion.findAccessLogsByAsset(asset);
   }

   @Override
   public CompletionStage<List<DataAccessLogEntryProperties>> findAccessLogsByUser(String userId) {
      return logsCompanion.findAccessLogsByUser(userId);
   }

   @Override
   public CompletionStage<List<DataAccessLogEntryProperties>> findAccessLogsByProject(UID project) {
      return logsCompanion.findAccessLogsByProject(project);
   }

   @Override
   public CompletionStage<List<DataAccessLogEntryProperties>> findAllAccessLogs() {
      return logsCompanion.findAllAccessLogs();
   }

}
