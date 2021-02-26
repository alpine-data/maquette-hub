package maquette.core.services.dependencies;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.DataAssetEntities;
import maquette.core.entities.data.DataAssetEntity;
import maquette.core.entities.data.collections.CollectionEntities;
import maquette.core.entities.data.datasets.DatasetEntities;
import maquette.core.entities.data.datasources.DataSourceEntities;
import maquette.core.entities.data.streams.StreamEntities;
import maquette.core.entities.dependencies.Dependencies;
import maquette.core.entities.dependencies.model.*;
import maquette.core.entities.dependencies.neo4j.Graph;
import maquette.core.entities.projects.ApplicationEntity;
import maquette.core.entities.projects.ModelEntity;
import maquette.core.entities.projects.ProjectEntities;
import maquette.core.entities.projects.ProjectEntity;
import maquette.core.entities.users.UserEntities;
import maquette.core.entities.users.UserEntity;
import maquette.core.services.dependencies.model.*;
import maquette.core.values.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DependencyServicesImpl implements DependencyServices {

   private static final Logger LOG = LoggerFactory.getLogger(DependencyServicesImpl.class);

   private final Dependencies dependencies;

   private final ProjectEntities projects;

   private final DatasetEntities datasets;

   private final CollectionEntities collections;

   private final DataSourceEntities dataSources;

   private final StreamEntities streams;

   private final UserEntities users;

   @Override
   public CompletionStage<Graph<DependencyPropertiesNode>> getDependencyGraph(
      User executor, DataAssetType type, String assetName) {

      return getDataAssetEntitiesForType(type)
         .getByName(assetName)
         .thenCompose(asset -> {
            var node = DataAssetNode.apply(type, asset.getId());
            return dependencies.getDependencyGraph(node);
         })
         .thenCompose(graph -> {
            var nodesMappedCS = Operators.allOf(graph
               .getNodes()
               .stream()
               .map(node -> {
                  if (node.getProperties() instanceof ApplicationNode) {
                     var n = (ApplicationNode) node.getProperties();
                     return projects
                        .getProjectById(n.getProject())
                        .thenCompose(ProjectEntity::getApplications)
                        .thenCompose(apps -> apps.getApplicationById(n.getId()))
                        .thenCompose(ApplicationEntity::getProperties)
                        .thenApply(p -> (DependencyPropertiesNode) ApplicationPropertiesNode.apply(n.getProject(), n.getId(), p))
                        .exceptionally(ex -> {
                           LOG.warn("Issue collecting node information for application {}/{}", n.getProject(), n.getId(), ex);
                           return ApplicationPropertiesNode.apply(n.getProject(), n.getId(), null);
                        })
                        .thenApply(node::withProperties);
                  } else if (node.getProperties() instanceof DataAssetNode) {
                     var n = (DataAssetNode) node.getProperties();
                     return getDataAssetEntitiesForType(n.getType())
                        .getById(n.getId())
                        .thenCompose(DataAssetEntity::getProperties)
                        .thenApply(p -> (DependencyPropertiesNode) DataAssetPropertiesNode.apply(n.getType(), n.getId(), p))
                        .exceptionally(ex -> {
                           LOG.warn("Issue collecting node information for data asset {}/{}", n.getType(), n.getId(), ex);
                           return DataAssetPropertiesNode.apply(n.getType(), n.getId(), null);
                        })
                        .thenApply(node::withProperties);
                  } else if (node.getProperties() instanceof ModelNode) {
                     var n = (ModelNode) node.getProperties();
                     return projects
                        .getProjectById(n.getProject())
                        .thenCompose(ProjectEntity::getModels)
                        .thenApply(models -> models.getModel(n.getName()))
                        .thenCompose(ModelEntity::getProperties)
                        .thenApply(p -> (DependencyPropertiesNode) ModelPropertiesNode.apply(n.getProject(), n.getName(), p))
                        .exceptionally(ex -> {
                           LOG.warn("Issue collecting node information for model {}/{}", n.getProject(), n.getName(), ex);
                           return ModelPropertiesNode.apply(n.getProject(), n.getName(), null);
                        })
                        .thenApply(node::withProperties);
                  } else if (node.getProperties() instanceof ProjectNode) {
                     var n = (ModelNode) node.getProperties();
                     return projects
                        .getProjectById(n.getProject())
                        .thenCompose(ProjectEntity::getProperties)
                        .thenApply(p -> (DependencyPropertiesNode) ProjectPropertiesNode.apply(n.getProject(), p))
                        .exceptionally(ex -> {
                           LOG.warn("Issue collecting node information for project {}", n.getProject(), ex);
                           return ProjectPropertiesNode.apply(n.getProject(), null);
                        })
                        .thenApply(node::withProperties);
                  } else if (node.getProperties() instanceof UserNode) {
                     var n = (UserNode) node.getProperties();
                     return users
                        .findUserById(n.getId())
                        .thenCompose(UserEntity::getProfile)
                        .thenApply(p -> (DependencyPropertiesNode) UserPropertiesNode.apply(n.getId(), p))
                        .exceptionally(ex -> {
                           LOG.warn("Issue collecting node information for user {}", n.getId(), ex);
                           return UserPropertiesNode.apply(n.getId(), null);
                        })
                        .thenApply(node::withProperties);
                  } else {
                     throw new RuntimeException("Unknown node type");
                  }
               }));

            return nodesMappedCS.thenApply(graph::withNodes);
         });
   }

   @Override
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

   @Override
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

   @Override
   public CompletionStage<Done> trackConsumptionByProject(
      User executor, DataAssetType type, String assetName, String projectName, String userId) {

      var projectCS = projects
         .getProjectByName(projectName);

      var assetCS = getDataAssetEntitiesForType(type)
         .getByName(assetName);

      return Operators
         .compose(
            projectCS, assetCS, (project, asset) -> {
               var assetNode = DataAssetNode.apply(type, asset.getId());
               var userNode = UserNode.apply(userId);
               var projectNode = ProjectNode.apply(project.getId());

               return dependencies.trackConsumption(assetNode, userNode, projectNode);
            })
         .thenCompose(cs -> cs);
   }

   @Override
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

   @Override
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

   @Override
   public CompletionStage<Done> trackProductionByProject(
      User executor, DataAssetType type, String assetName, String projectName, String userId) {

      var projectCS = projects
         .getProjectByName(projectName);

      var assetCS = getDataAssetEntitiesForType(type)
         .getByName(assetName);

      return Operators
         .compose(
            projectCS, assetCS, (project, asset) -> {
               var assetNode = DataAssetNode.apply(type, asset.getId());
               var userNode = UserNode.apply(userId);
               var projectNode = ProjectNode.apply(project.getId());

               return dependencies.trackProduction(assetNode, userNode, projectNode);
            })
         .thenCompose(cs -> cs);
   }

   @Override
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

   private DataAssetEntities<?, ?> getDataAssetEntitiesForType(DataAssetType type) {
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
