package maquette.core.services.dependencies;

import akka.Done;
import maquette.core.entities.dependencies.neo4j.Graph;
import maquette.core.services.dependencies.model.DependencyPropertiesNode;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

public interface DependencyServices {

   CompletionStage<Graph<DependencyPropertiesNode>> getDependencyGraph(
      User executor, String assetName);

   CompletionStage<Done> trackConsumptionByApplication(
      User executor, String assetName, String projectName, String applicationName);

   CompletionStage<Done> trackConsumptionByModel(
      User executor, String assetName, String projectName, String modelName);

   CompletionStage<Done> trackConsumptionByProject(
      User executor, String assetName, String projectName);

   CompletionStage<Done> trackProductionByApplication(
      User executor, String assetName, String projectName, String applicationName);

   CompletionStage<Done> trackProductionByUser(
      User executor, String assetName, String userId);

   CompletionStage<Done> trackProductionByProject(
      User executor, String assetName, String projectName);

   CompletionStage<Done> trackModelUsageByApplication(
      User executor, String projectName, String modelName, String applicationProjectName, String applicationName);

}
