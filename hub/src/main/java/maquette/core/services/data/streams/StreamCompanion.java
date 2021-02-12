package maquette.core.services.data.streams;

import akka.japi.Pair;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.streams.StreamEntities;
import maquette.core.entities.data.streams.StreamEntity;
import maquette.core.entities.data.streams.model.Stream;
import maquette.core.entities.data.streams.model.StreamProperties;
import maquette.core.services.ServiceCompanion;
import maquette.core.services.data.DataAssetCompanion;

import java.util.Comparator;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class StreamCompanion extends ServiceCompanion {

   private final DataAssetCompanion<StreamProperties, StreamEntities> assets;

   public CompletionStage<Stream> mapEntityToAsset(StreamEntity asset) {
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

      return Operators.compose(propertiesCS, membersCS, accessRequestsCS, (p, members, accessRequests) ->
         Stream.apply(
            p.getId(), p.getTitle(), p.getName(), p.getSummary(), p.getRetention(), p.getSchema().orElse(null),
            p.getVisibility(), p.getClassification(), p.getPersonalInformation(),
            p.getZone(), p.getState(), 0, false, p.getCreated(), p.getUpdated(),
            members, accessRequests));
   }

}
