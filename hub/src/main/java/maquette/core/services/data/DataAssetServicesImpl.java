package maquette.core.services.data;

import akka.Done;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.assets.DataAssetEntities;
import maquette.core.entities.data.assets.DataAssetEntity;
import maquette.core.entities.data.datasets.model.tasks.ReviewAccessRequest;
import maquette.core.entities.data.datasets.model.tasks.ReviewDataAsset;
import maquette.core.entities.data.datasets.model.tasks.Task;
import maquette.core.entities.logs.LogEntry;
import maquette.core.entities.projects.ProjectEntities;
import maquette.core.services.logs.LogsCompanion;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.access.DataAccessRequestProperties;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.data.DataAsset;
import maquette.core.values.data.DataAssetMemberRole;
import maquette.core.values.data.DataAssetProperties;
import maquette.core.values.data.DataAssetState;
import maquette.core.values.user.User;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DataAssetServicesImpl<P extends DataAssetProperties<P>, E extends DataAssetEntity<P>, EN extends DataAssetEntities<P, E>> implements DataAssetServices<P, E> {

   private final EN assets;

   private final ProjectEntities projects;

   private final DataAssetCompanion<P, EN> companion;

   private final LogsCompanion logs;

   public static <P extends DataAssetProperties<P>, E extends DataAssetEntity<P>, EN extends DataAssetEntities<P, E>> DataAssetServicesImpl<P, E, EN> apply(
      EN assets, ProjectEntities projects, DataAssetCompanion<P, EN> companion, LogsCompanion logs) {

      return new DataAssetServicesImpl<>(assets, projects, companion, logs);
   }

   @Override
   public CompletionStage<Done> grant(User executor, String asset, Authorization member, DataAssetMemberRole role) {
      return assets
         .getByName(asset)
         .thenCompose(a -> a.getMembers().addMember(executor, member, role));
   }

   @Override
   public CompletionStage<Done> revoke(User executor, String asset, Authorization member) {
      return assets
         .getByName(asset)
         .thenCompose(a -> a.getMembers().removeMember(executor, member));
   }

   @Override
   public CompletionStage<List<LogEntry>> getAccessLogs(User executor, String asset) {
      return assets
         .getResourceUID(asset)
         .thenCompose(logs::getLogsByResourcePrefix);
   }

   @Override
   public CompletionStage<DataAccessRequestProperties> createDataAccessRequest(User executor, String asset, String project, String reason) {
      var asCS = assets.getByName(asset);
      var prCS = projects.getProjectByName(project);

      return Operators
         .compose(asCS, prCS, (as, pr) -> as
            .getAccessRequests()
            .createDataAccessRequest(executor, pr.getId(), reason))
         .thenCompose(cs -> cs);
   }

   @Override
   public <T extends DataAsset<T>> CompletionStage<T> get(User executor, String asset, Function<E, CompletionStage<T>> mapEntityToAsset) {
      return assets
         .getByName(asset)
         .thenCompose(mapEntityToAsset);
   }

   @Override
   public CompletionStage<List<P>> list(User executor) {
      return assets.list();
   }

   @Override
   public CompletionStage<Done> remove(User executor, String asset) {
      return assets
         .findByName(asset)
         .thenCompose(maybeEntity -> {
            if (maybeEntity.isPresent()) {
               return assets.remove(maybeEntity.get().getId());
            } else {
               return CompletableFuture.completedFuture(Done.getInstance());
            }
         });
   }

   @Override
   public CompletionStage<Done> approve(User executor, String asset) {
      return assets
         .getByName(asset)
         .thenCompose(e -> e.approve(executor));
   }

   @Override
   public CompletionStage<Done> deprecate(User executor, String asset, boolean deprecate) {
      return assets
         .getByName(asset)
         .thenCompose(ds -> ds.deprecate(executor, deprecate));
   }

   @Override
   public CompletionStage<List<Task>> getOpenTasks(User executor, String asset) {
      return assets
         .getByName(asset)
         .thenCompose(entity -> {
            var propertiesCS = entity
               .getProperties();

            var openAccessRequestsCS = entity
               .getAccessRequests()
               .getOpenDataAccessRequests();

            var reviewAccessRequestsCS = Operators
               .compose(propertiesCS, openAccessRequestsCS, (properties, openAccessRequests) -> openAccessRequests
                  .stream()
                  .map(request -> companion
                     .enrichDataAccessRequest(properties, request)
                     .thenApply(r -> (Task) ReviewAccessRequest.apply(r.getAsset().getTitle(), r.getProject().getTitle(), r.getId()))))
               .thenCompose(Operators::allOf);

            var reviewAssetCS = propertiesCS.thenApply(properties -> {
               if (properties.getState().equals(DataAssetState.REVIEW_REQUIRED)) {
                  return List.of((Task) ReviewDataAsset.apply(properties));
               } else {
                  return List.<Task>of();
               }
            });

            return Operators.compose(reviewAccessRequestsCS, reviewAssetCS, (reviewAccessRequests, reviewAsset) -> Stream
               .concat(reviewAccessRequests.stream(), reviewAsset.stream())
               .collect(Collectors.toList()));
         });
   }


   @Override
   public CompletionStage<DataAccessRequest> getDataAccessRequest(User executor, String asset, UID request) {
      var assetEntityCS = assets.getByName(asset);
      var assetPropertiesCS = assetEntityCS.thenCompose(DataAssetEntity::getProperties);
      var accessRequestPropertiesCS = assetEntityCS.thenCompose(a -> a.getAccessRequests().getDataAccessRequestById(request));

      return Operators
         .compose(assetPropertiesCS, accessRequestPropertiesCS, companion::enrichDataAccessRequest)
         .thenCompose(cs -> cs);
   }

   @Override
   public CompletionStage<Done> grantDataAccessRequest(User executor, String asset, UID request, @Nullable Instant until, @Nullable String message) {
      return assets
         .getByName(asset)
         .thenCompose(a -> a.getAccessRequests().grantDataAccessRequest(executor, request, until, message));
   }

   @Override
   public CompletionStage<Done> rejectDataAccessRequest(User executor, String asset, UID request, String reason) {
      return assets
         .getByName(asset)
         .thenCompose(a -> a.getAccessRequests().rejectDataAccessRequest(executor, request, reason));
   }

   @Override
   public CompletionStage<Done> updateDataAccessRequest(User executor, String asset, UID request, String reason) {
      return assets
         .getByName(asset)
         .thenCompose(a -> a.getAccessRequests().updateDataAccessRequest(executor, request, reason));
   }

   @Override
   public CompletionStage<Done> withdrawDataAccessRequest(User executor, String asset, UID request, @Nullable String reason) {
      return assets
         .getByName(asset)
         .thenCompose(a -> a.getAccessRequests().withdrawDataAccessRequest(executor, request, reason));
   }

}
