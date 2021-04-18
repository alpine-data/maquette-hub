package maquette.core.services.dependencies;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.DataAssetEntities;
import maquette.core.entities.data.DataAssetEntity;
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

import java.util.Objects;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class DependencyServicesImpl implements DependencyServices {

   private static final Logger LOG = LoggerFactory.getLogger(DependencyServicesImpl.class);

   private final DependencyCompanion comp;

   private final Dependencies dependencies;

   private final DataAssetEntities assets;

   private final ProjectEntities projects;

   private final UserEntities users;

   @Override
   public CompletionStage<Graph<DependencyPropertiesNode>> getDependencyGraph(User executor, String assetName) {

      return assets
         .getByName(assetName)
         .thenCompose(DataAssetEntity::getProperties)
         .thenCompose(asset -> {
            var node = DataAssetNode.apply(asset.getType(), asset.getId());
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
                           LOG.trace("Issue collecting node information for application {}/{}", n.getProject(), n.getId(), ex);
                           return null;
                        })
                        .thenApply(node::withProperties);
                  } else if (node.getProperties() instanceof DataAssetNode) {
                     var n = (DataAssetNode) node.getProperties();
                     return assets
                        .getById(n.getId())
                        .getProperties()
                        .thenApply(p -> (DependencyPropertiesNode) DataAssetPropertiesNode.apply(n.getType(), n.getId(), p))
                        .exceptionally(ex -> {
                           LOG.trace("Issue collecting node information for data asset {}/{}", n.getType(), n.getId(), ex);
                           return null;
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
                           LOG.trace("Issue collecting node information for model {}/{}", n.getProject(), n.getName(), ex);
                           return null;
                        })
                        .thenApply(node::withProperties);
                  } else if (node.getProperties() instanceof ProjectNode) {
                     var n = (ProjectNode) node.getProperties();
                     return projects
                        .getProjectById(n.getProject())
                        .thenCompose(ProjectEntity::getProperties)
                        .thenApply(p -> (DependencyPropertiesNode) ProjectPropertiesNode.apply(n.getProject(), p))
                        .exceptionally(ex -> {
                           LOG.trace("Issue collecting node information for project {}", n.getProject(), ex);
                           return null;
                        })
                        .thenApply(node::withProperties);
                  } else if (node.getProperties() instanceof UserNode) {
                     var n = (UserNode) node.getProperties();
                     return users
                        .findUserById(n.getId())
                        .thenCompose(UserEntity::getProfile)
                        .thenApply(p -> (DependencyPropertiesNode) UserPropertiesNode.apply(n.getId(), p))
                        .exceptionally(ex -> {
                           LOG.trace("Issue collecting node information for user {}", n.getId(), ex);
                           return null;
                        })
                        .thenApply(node::withProperties);
                  } else {
                     throw new RuntimeException("Unknown node type");
                  }
               }));

            return nodesMappedCS
               .thenApply(nodes -> nodes
                  .stream()
                  .filter(node -> node.getProperties() != null)
                  .collect(Collectors.toList()))
               .thenApply(graph::withNodes);
         });
   }

   @Override
   public CompletionStage<Done> trackConsumptionByApplication(
      User executor, String assetName, String projectName, String applicationName) {

      return comp.trackConsumptionByApplication(executor, assetName, projectName, applicationName);
   }

   @Override
   public CompletionStage<Done> trackConsumptionByModel(
      User executor, String assetName, String projectName, String modelName) {

      return comp.trackConsumptionByModel(executor, assetName, projectName, modelName);
   }

   @Override
   public CompletionStage<Done> trackConsumptionByProject(
      User executor, String assetName, String projectName) {

      return comp.trackConsumptionByProject(executor, assetName, projectName);
   }

   @Override
   public CompletionStage<Done> trackProductionByApplication(
      User executor, String assetName, String projectName, String applicationName) {

      return comp.trackProductionByApplication(executor, assetName, projectName, applicationName);
   }

   @Override
   public CompletionStage<Done> trackProductionByUser(
      User executor, String assetName, String userId) {

      return comp.trackProductionByUser(executor, assetName, userId);
   }

   @Override
   public CompletionStage<Done> trackProductionByProject(
      User executor, String assetName, String projectName) {

      return comp.trackProductionByProject(executor, assetName, projectName);
   }

   @Override
   public CompletionStage<Done> trackModelUsageByApplication(
      User executor, String projectName, String modelName, String applicationProjectName, String applicationName) {

      return comp.trackModelUsageByApplication(executor, projectName, modelName, applicationProjectName, applicationName);
   }


}
