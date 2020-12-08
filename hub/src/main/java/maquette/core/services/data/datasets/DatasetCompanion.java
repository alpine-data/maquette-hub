package maquette.core.services.data.datasets;

import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.datasets.DatasetEntities;
import maquette.core.entities.data.datasets.DatasetEntity;
import maquette.core.entities.data.datasets.model.Dataset;
import maquette.core.entities.data.datasets.model.DatasetProperties;
import maquette.core.services.ServiceCompanion;
import maquette.core.services.data.DataAssetCompanion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class DatasetCompanion extends ServiceCompanion {

   @SuppressWarnings("unused")
   private static final Logger LOG = LoggerFactory.getLogger(DatasetServices.class);

   private final DataAssetCompanion<DatasetProperties, DatasetEntities> assets;

   public CompletionStage<Dataset> mapEntityToDataset(DatasetEntity dataset) {
      return dataset
         .getProperties()
         .thenCompose(properties -> {
            var membersCS = dataset
               .getMembers()
               .getMembers()
               .thenApply(members -> members
                  .stream()
                  .sorted(Comparator.comparing(granted -> granted.getAuthorization().getName()))
                  .collect(Collectors.toList()));

            var versionsCS = dataset.getRevisions().getVersions();

            var accessRequestsCS = dataset
               .getAccessRequests()
               .getDataAccessRequests()
               .thenCompose(requests -> Operators.allOf(
                  requests
                     .stream()
                     .map(request -> assets.enrichDataAccessRequest(properties, request))));

            return Operators
               .compose(
                  membersCS, accessRequestsCS, versionsCS,
                  (members, requests, versions) -> Dataset.apply(
                     dataset.getId(), properties.getTitle(), properties.getName(), properties.getSummary(),
                     properties.getVisibility(), properties.getClassification(), properties.getPersonalInformation(),
                     properties.getCreated(), properties.getUpdated(),
                     members, requests, List.of(), versions));
         });
   }

}
