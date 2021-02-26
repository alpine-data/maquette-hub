package maquette.core.services.dependencies;

import akka.Done;
import maquette.core.entities.dependencies.model.DataAssetType;
import maquette.core.entities.dependencies.neo4j.Graph;
import maquette.core.services.dependencies.model.DependencyPropertiesNode;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

public interface DependencyServices {

   CompletionStage<Graph<DependencyPropertiesNode>> getDependencyGraph(
      User executor, DataAssetType type, String assetName);

   CompletionStage<Done> trackConsumptionByApplication(
      User executor, DataAssetType type, String assetName, String projectName, String applicationName);

   CompletionStage<Done> trackConsumptionByModel(
      User executor, DataAssetType type, String assetName, String projectName, String modelName);

   CompletionStage<Done> trackConsumptionByProject(
      User executor, DataAssetType type, String assetName, String projectName, String userId);

   CompletionStage<Done> trackProductionByApplication(
      User executor, DataAssetType type, String assetName, String projectName, String applicationName);

   CompletionStage<Done> trackProductionByUser(
      User executor, DataAssetType type, String assetName, String userId);

   CompletionStage<Done> trackProductionByProject(
      User executor, DataAssetType type, String assetName, String projectName, String userId);

   CompletionStage<Done> trackModelUsageByApplication(
      User executor, String projectName, String modelName, String applicationProjectName, String applicationName);

}
