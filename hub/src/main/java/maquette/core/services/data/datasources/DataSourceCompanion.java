package maquette.core.services.data.datasources;

import akka.japi.Pair;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.datasources.DataSourceEntities;
import maquette.core.entities.data.datasources.DataSourceEntity;
import maquette.core.entities.data.datasources.model.DataSource;
import maquette.core.entities.data.datasources.model.DataSourceProperties;
import maquette.core.services.ServiceCompanion;
import maquette.core.services.data.DataAssetCompanion;

import java.util.Comparator;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class DataSourceCompanion extends ServiceCompanion {

   private final DataAssetCompanion<DataSourceProperties, DataSourceEntities> assets;

   public CompletionStage<DataSource> mapEntityToDataSource(DataSourceEntity dataSource) {
      var propertiesCS = dataSource.getProperties();

      var membersCS = dataSource
         .getMembers()
         .getMembers()
         .thenApply(members -> members
            .stream()
            .sorted(Comparator.comparing(granted -> granted.getAuthorization().getName()))
            .collect(Collectors.toList()));

      var accessRequestsCS = dataSource
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

      return Operators.compose(propertiesCS, membersCS, accessRequestsCS, (p, members, accessRequests) -> DataSource.apply(
         p.getId(), p.getTitle(), p.getName(), p.getSummary(), p.getDatabase(), p.getAccessType(),
         p.getVisibility(), p.getClassification(), p.getPersonalInformation(),
         p.getZone(), p.getState(), 0, false,
         p.getSchema(), p.getFetched(), p.getRecords(),
         p.getCreated(), p.getUpdated(), members, accessRequests));
   }

}
