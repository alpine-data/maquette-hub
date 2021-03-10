package maquette.core.services.dependencies;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.assets.DataAssetEntities;
import maquette.core.entities.data.assets.DataAssetEntity;
import maquette.core.entities.data.collections.CollectionEntities;
import maquette.core.entities.data.datasets.DatasetEntities;
import maquette.core.entities.data.datasources.DataSourceEntities;
import maquette.core.entities.data.streams.StreamEntities;
import maquette.core.entities.dependencies.Dependencies;
import maquette.core.entities.dependencies.model.*;
import maquette.core.entities.projects.ProjectEntities;
import maquette.core.entities.projects.ProjectEntity;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DependencyCompanion {

   private final Dependencies dependencies;

   private final ProjectEntities projects;

   private final DatasetEntities datasets;

   private final CollectionEntities collections;

   private final DataSourceEntities dataSources;

   private final StreamEntities streams;

   public CompletionStage<Done> trackConsumptionByApplication(
      User executor, DataAssetType type, String assetName, String projectName, String applicationName) {
      var projectCS = projects
         .getProjectByName(projectName);

      var appCS = projectCS
         .thenCompose(ProjectEntity::getApplications)
         .thenCompose(entities -> entities.getApplicationByName(applicationName));

      var assetCS = getDataAssetEntitiesForType(type)
         .getByName(assetName);

      return Operators
         .compose(projectCS, appCS, assetCS, (project, app, asset) -> {
            var assetNode = DataAssetNode.apply(type, asset.getId());
            var appNode = ApplicationNode.apply(project.getId(), app.getId());
            return dependencies.trackConsumption(assetNode, appNode);
         })
         .thenCompose(cs -> cs);
   }

   public CompletionStage<Done> trackConsumptionByModel(
      User executor, DataAssetType type, String assetName, String projectName, String modelName) {

      var projectCS = projects
         .getProjectByName(projectName);

      var modelCS = projectCS
         .thenCompose(ProjectEntity::getModels)
         .thenApply(modelEntities -> modelEntities.getModel(modelName));

      var assetCS = getDataAssetEntitiesForType(type)
         .getByName(assetName);

      return Operators
         .compose(projectCS, modelCS, assetCS, (project, model, asset) -> {
            var assetNode = DataAssetNode.apply(type, asset.getId());
            var modelNode = ModelNode.apply(project.getId(), model.getName());
            return dependencies.trackConsumption(assetNode, modelNode);
         })
         .thenCompose(cs -> cs);
   }

   public CompletionStage<Done> trackConsumptionByProject(
      User executor, DataAssetType type, String assetName, String projectName) {

      var projectCS = projects
         .getProjectByName(projectName);

      var assetCS = getDataAssetEntitiesForType(type)
         .getByName(assetName);

      return Operators
         .compose(
            projectCS, assetCS, (project, asset) -> {
               var assetNode = DataAssetNode.apply(type, asset.getId());
               var projectNode = ProjectNode.apply(project.getId());

               return dependencies.trackConsumption(assetNode, projectNode);
            })
         .thenCompose(cs -> cs);
   }

   public CompletionStage<Done> trackProductionByApplication(
      User executor, DataAssetType type, String assetName, String projectName, String applicationName) {

      var projectCS = projects
         .getProjectByName(projectName);

      var appCS = projectCS
         .thenCompose(ProjectEntity::getApplications)
         .thenCompose(entities -> entities.getApplicationByName(applicationName));

      var assetCS = getDataAssetEntitiesForType(type)
         .getByName(assetName);

      return Operators
         .compose(projectCS, appCS, assetCS, (project, app, asset) -> {
            var assetNode = DataAssetNode.apply(type, asset.getId());
            var appNode = ApplicationNode.apply(project.getId(), app.getId());
            return dependencies.trackProduction(assetNode, appNode);
         })
         .thenCompose(cs -> cs);
   }

   public CompletionStage<Done> trackProductionByUser(
      User executor, DataAssetType type, String assetName, String userId) {

      return getDataAssetEntitiesForType(type)
         .getByName(assetName)
         .thenApply(DataAssetEntity::getId)
         .thenCompose(asset -> {
            var assetNode = DataAssetNode.apply(type, asset);
            var userNode = UserNode.apply(userId);
            return dependencies.trackProduction(assetNode, userNode);
         });
   }

   public CompletionStage<Done> trackProductionByProject(
      User executor, DataAssetType type, String assetName, String projectName) {

      var projectCS = projects
         .getProjectByName(projectName);

      var assetCS = getDataAssetEntitiesForType(type)
         .getByName(assetName);

      return Operators
         .compose(
            projectCS, assetCS, (project, asset) -> {
               var assetNode = DataAssetNode.apply(type, asset.getId());
               var projectNode = ProjectNode.apply(project.getId());

               return dependencies.trackProduction(assetNode, projectNode);
            })
         .thenCompose(cs -> cs);
   }

   public CompletionStage<Done> trackModelUsageByApplication(
      User executor, String projectName, String modelName, String applicationProjectName, String applicationName) {

      var modelCS = projects
         .getProjectByName(projectName)
         .thenCompose(ProjectEntity::getModels)
         .thenApply(models -> models.getModel(modelName));

      var applicationCS = projects
         .getProjectByName(applicationProjectName)
         .thenCompose(ProjectEntity::getApplications)
         .thenCompose(apps -> apps.getApplicationByName(applicationName));

      return Operators
         .compose(modelCS, applicationCS, (model, application) -> {
            var modelNode = ModelNode.apply(model.getProject(), model.getName());
            var applicationNode = ApplicationNode.apply(application.getProject(), application.getId());
            return dependencies.trackUsage(modelNode, applicationNode);
         })
         .thenCompose(cs -> cs);
   }

   public DataAssetEntities<?, ?> getDataAssetEntitiesForType(DataAssetType type) {
      switch (type) {
         case DATASET:
            return datasets;
         case COLLECTION:
            return collections;
         case SOURCE:
            return dataSources;
         case STREAM:
         default:
            return streams;
      }
   }

}
