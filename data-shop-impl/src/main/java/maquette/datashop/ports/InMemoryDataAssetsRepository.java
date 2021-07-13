package maquette.datashop.ports;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.datashop.values.DataAssetProperties;
import maquette.datashop.values.access.DataAssetMemberRole;
import maquette.datashop.values.access_requests.DataAccessRequestProperties;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

/**
 * An in-memory implementation of the {@link DataAssetsRepository} to showcase expected behavior and for
 * simple tests.
 */
@AllArgsConstructor(staticName = "apply")
public final class InMemoryDataAssetsRepository implements DataAssetsRepository {

   @Override
   public CompletionStage<List<GrantedAuthorization<DataAssetMemberRole>>> findAllMembers(UID parent) {
      return null;
   }

   @Override
   public CompletionStage<List<GrantedAuthorization<DataAssetMemberRole>>> findMembersByRole(UID parent, DataAssetMemberRole role) {
      return null;
   }

   @Override
   public CompletionStage<Done> insertOrUpdateMember(UID parent, GrantedAuthorization<DataAssetMemberRole> member) {
      return null;
   }

   @Override
   public CompletionStage<Done> removeMember(UID parent, Authorization member) {
      return null;
   }

   @Override
   public CompletionStage<Optional<DataAssetProperties>> findDataAssetByName(String name) {
      return null;
   }

   @Override
   public CompletionStage<Optional<DataAssetProperties>> findEntitiesById(UID id) {
      return null;
   }

   @Override
   public <T> CompletionStage<Optional<T>> fetchCustomSettings(UID id, Class<T> expectedType) {
      return null;
   }

   @Override
   public <T> CompletionStage<Optional<T>> fetchCustomProperties(UID id, Class<T> expectedType) {
      return null;
   }

   @Override
   public CompletionStage<Done> insertOrUpdateDataAsset(DataAssetProperties updated) {
      return null;
   }

   @Override
   public CompletionStage<Done> insertOrUpdateCustomSettings(UID id, Object customSettings) {
      return null;
   }

   @Override
   public CompletionStage<Done> insertOrUpdateCustomProperties(UID id, Object customProperties) {
      return null;
   }

   @Override
   public CompletionStage<Stream<DataAssetProperties>> listEntities() {
      return null;
   }

   @Override
   public CompletionStage<Done> removeDataAssetById(UID id) {
      return null;
   }

   @Override
   public CompletionStage<Optional<DataAccessRequestProperties>> findDataAccessRequestById(UID asset, UID request) {
      return null;
   }

   @Override
   public CompletionStage<Done> insertOrUpdateDataAccessRequest(DataAccessRequestProperties request) {
      return null;
   }

   @Override
   public CompletionStage<List<DataAccessRequestProperties>> findDataAccessRequestsByWorkspace(UID workspace) {
      return null;
   }

   @Override
   public CompletionStage<List<DataAccessRequestProperties>> findDataAccessRequestsByAsset(UID asset) {
      return null;
   }

   @Override
   public CompletionStage<Done> removeDataAccessRequest(UID asset, UID id) {
      return null;
   }

}
