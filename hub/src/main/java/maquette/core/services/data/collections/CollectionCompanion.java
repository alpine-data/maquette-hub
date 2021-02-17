package maquette.core.services.data.collections;

import akka.japi.Pair;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.collections.CollectionEntities;
import maquette.core.entities.data.collections.CollectionEntity;
import maquette.core.entities.data.collections.model.Collection;
import maquette.core.entities.data.collections.model.CollectionProperties;
import maquette.core.services.ServiceCompanion;
import maquette.core.services.data.DataAssetCompanion;

import java.util.Comparator;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class CollectionCompanion extends ServiceCompanion {

   private final DataAssetCompanion<CollectionProperties, CollectionEntities> assets;

   public CompletionStage<Collection> mapEntityToAsset(CollectionEntity asset) {
      var propertiesCS = asset.getProperties();

      var membersCS = asset
         .getMembers()
         .getMembers()
         .thenApply(members -> members
            .stream()
            .sorted(Comparator.comparing(granted -> granted.getAuthorization().getName()))
            .collect(Collectors.toList()));

      var accessRequestsCS = asset
         .getAccessRequests()
         .getDataAccessRequests()
         .thenCompose(requests -> propertiesCS.thenApply(properties -> Pair.apply(requests, properties)))
         .thenApply(pair -> {
            var requests = pair.first();
            var properties = pair.second();

            return requests
               .stream()
               .map(request -> assets.enrichDataAccessRequest(properties, request));
         })
         .thenCompose(Operators::allOf);

      var tagsCS = asset
         .getFiles()
         .getTags();

      return Operators.compose(propertiesCS, membersCS, accessRequestsCS, tagsCS, (p, members, accessRequests, tags) ->
         Collection.apply(
            p.getId(), p.getTitle(), p.getName(), p.getSummary(), p.getFiles(), tags,
            p.getVisibility(), p.getClassification(), p.getPersonalInformation(),
            p.getZone(), p.getState(), 0, false, p.getCreated(), p.getUpdated(),
            members, accessRequests));
   }

}
