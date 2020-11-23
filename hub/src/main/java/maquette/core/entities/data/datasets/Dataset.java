package maquette.core.entities.data.datasets;

import akka.Done;
import lombok.AllArgsConstructor;
import lombok.Getter;
import maquette.common.Operators;
import maquette.core.entities.data.datasets.exceptions.DatasetNotFoundException;
import maquette.core.entities.data.datasets.model.DatasetDetails;
import maquette.core.entities.data.datasets.model.DatasetProperties;
import maquette.core.ports.DatasetsStore;
import maquette.core.ports.DatasetsRepository;
import maquette.core.values.ActionMetadata;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.data.DataClassification;
import maquette.core.values.data.DataVisibility;
import maquette.core.values.data.PersonalInformation;
import maquette.core.values.user.User;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

@Getter
@AllArgsConstructor(staticName = "apply")
public final class Dataset {

   private final String id;

   private final String projectId;

   private final String name;

   private final DatasetsRepository repository;

   private final DatasetsStore store;

   private String getFullId() {
      return String.format("%s/%s", projectId, id);
   }

   public CompletionStage<Done> addOwner(User executor, UserAuthorization owner) {
      return repository
         .findAllOwners(getFullId())
         .thenCompose(existing -> {
            if (existing.contains(owner)) {
               return CompletableFuture.completedFuture(Done.getInstance());
            } else {
               return repository.insertOwner(getFullId(), owner);
            }
         });
   }

   public AccessRequests accessRequests() {
      return AccessRequests.apply(id, projectId, getFullId(), name, repository);
   }

   public AccessTokens accessTokens() {
      return AccessTokens.apply(id, projectId, getFullId(), name, repository);
   }

   public Revisions revisions() {
      return Revisions.apply(id, projectId, getFullId(), name, repository, store);
   }

   public CompletionStage<DatasetProperties> getProperties() {
      return withDatasetProperties(CompletableFuture::completedFuture);
   }

   public CompletionStage<DatasetDetails> getDatasetDetails() {
      return withDatasetProperties(p -> {
         var getOwners = repository.findAllOwners(getFullId());
         var getAccessRequests = accessRequests().getDataAccessRequests();
         var getAccessTokens = accessTokens().getDataAccessTokens();

         return Operators
            .compose(getOwners, getAccessRequests, getAccessTokens, (owners, requests, tokens) -> DatasetDetails.apply(
               id, p.getTitle(), p.getName(), p.getSummary(), p.getDescription(), p.getVisibility(), p.getClassification(),
               p.getPersonalInformation(), p.getCreated(), p.getUpdated(), owners, requests, tokens));
      });
   }

   public CompletionStage<Done> removeOwner(User executor, UserAuthorization owner) {
      return repository
         .findAllOwners(getFullId())
         .thenCompose(owners -> {
            if (owners.contains(owner) && owners.size() == 1) {
               // TODO
               throw new RuntimeException("Cannot remove last remaining owner from Project.");
            } else if (owners.contains(owner)) {
               return repository.removeOwner(getFullId(), owner.getUser());
            } else {
               return CompletableFuture.completedFuture(Done.getInstance());
            }
         });
   }

   public CompletionStage<Done> updateDetails(
      User executor, String name, String title, String summary,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation) {
      // TODO mw: value validation ...

      return withDatasetProperties(properties -> {
         var updated = properties
            .withName(name)
            .withTitle(title)
            .withSummary(summary)
            .withVisibility(visibility)
            .withClassification(classification)
            .withPersonalInformation(personalInformation)
            .withUpdated(ActionMetadata.apply(executor));

         return repository.insertOrUpdateDataset(projectId, updated);
      });
   }

   private <T> CompletionStage<T> withDatasetProperties(Function<DatasetProperties, CompletionStage<T>> func) {
      return repository
         .findDatasetById(projectId, id)
         .thenCompose(maybeDataset -> {
            if (maybeDataset.isPresent()) {
               return func.apply(maybeDataset.get());
            } else {
               throw DatasetNotFoundException.apply(name);
            }
         });
   }

}
