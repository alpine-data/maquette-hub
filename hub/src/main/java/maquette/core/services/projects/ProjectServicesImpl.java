package maquette.core.services.projects;

import akka.Done;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.datasets.DatasetEntities;
import maquette.core.entities.data.datasets.DatasetEntity;
import maquette.core.entities.data.datasets.model.Dataset;
import maquette.core.entities.data.datasets.model.DatasetProperties;
import maquette.core.entities.data.datasources.DataSourceEntities;
import maquette.core.entities.data.datasources.model.DataSourceProperties;
import maquette.core.entities.infrastructure.Container;
import maquette.core.entities.infrastructure.Deployment;
import maquette.core.entities.infrastructure.InfrastructureManager;
import maquette.core.entities.infrastructure.model.ContainerConfig;
import maquette.core.entities.infrastructure.model.DeploymentConfig;
import maquette.core.entities.processes.ProcessManager;
import maquette.core.entities.projects.ProjectEntities;
import maquette.core.entities.projects.model.MlflowConfiguration;
import maquette.core.entities.projects.model.Project;
import maquette.core.entities.projects.model.ProjectMemberRole;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.entities.sandboxes.SandboxEntities;
import maquette.core.entities.sandboxes.model.stacks.Stack;
import maquette.core.entities.sandboxes.model.stacks.Stacks;
import maquette.core.services.data.datasets.DatasetCompanion;
import maquette.core.services.data.datasources.DataSourceCompanion;
import maquette.core.services.sandboxes.SandboxCompanion;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.data.DataAsset;
import maquette.core.values.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class ProjectServicesImpl implements ProjectServices {

   private static final Logger LOG = LoggerFactory.getLogger(ProjectServices.class);

   ProcessManager processes;

   ProjectEntities projects;

   DatasetEntities datasets;

   DataSourceEntities dataSources;

   SandboxEntities sandboxes;

   InfrastructureManager infrastructure;

   ProjectCompanion companion;

   DatasetCompanion datasetCompanion;

   DataSourceCompanion dataSourceCompanion;

   SandboxCompanion sandboxCompanion;

   @Override
   public CompletionStage<Done> create(User executor, String name, String title, String summary) {
      return projects
         .createProject(executor, name, title, summary)
         .thenCompose(project -> projects.getProjectById(project.getId()))
         .thenCompose(project -> project.members().addMember(executor, executor.toAuthorization(), ProjectMemberRole.ADMIN).thenApply(d -> project))
         .thenCompose(project -> {
            /*
             * Create MLflow deployment for project
             */
            var mlflowConfig = MlflowConfiguration.apply(project.getId());
            var mlflowDeploymentConfig = project
               .setMlflowConfiguration(mlflowConfig)
               .thenApply(done -> createMlflowDeploymentConfig(project.getId(), mlflowConfig));

            return mlflowDeploymentConfig
               .thenCompose(infrastructure::applyConfig)
               .thenCompose(done -> project.getProperties());
         })
         .thenCompose(project -> {
            /*
             * Configure Mlflow router for MLflow instance for this project.
             */
            var maybeMlflowConfig = project.getMlflowConfiguration();

            if (maybeMlflowConfig.isPresent()) {
               var mlflowConfig = maybeMlflowConfig.get();

               return infrastructure
                  .getDeployment(mlflowConfig.getDeploymentName())
                  .map(deployment -> deployment.getContainer(mlflowConfig.getMlflowContainerName(project.getId())))
                  .map(maybeContainer -> maybeContainer
                     .map(Container::getMappedPortUrls)
                     .orElse(CompletableFuture.completedFuture(Maps.newHashMap())))
                  .orElse(CompletableFuture.completedFuture(Maps.newHashMap()))
                  .thenCompose(mlflowPorts -> {
                     if (mlflowPorts.containsKey(5000)) {
                        return infrastructure.registerRoute(project.getId().getValue(), mlflowConfig.getMlflowBasePath(project.getId()), mlflowPorts.get(5000).toString());
                     } else {
                        LOG.warn("Unable to register MLflow routes for project `{}` - Missing MLflow port information.", project.getId());
                        return CompletableFuture.completedFuture(Done.getInstance());
                     }
                  });
            } else {
               return CompletableFuture.completedFuture(Done.getInstance());
            }
         })
         .thenApply(ignore -> Done.getInstance());
   }

   @Override
   public CompletionStage<Map<String, String>> environment(User user, String name) {
      return projects
         .getProjectByName(name)
         .thenCompose(project -> {
            var propertiesCS = project.getProperties();
            var deploymentOptCS = propertiesCS
               .thenApply(properties -> {
                  if (properties.getMlflowConfiguration().isPresent()) {
                     return infrastructure
                        .getDeployment(properties.getMlflowConfiguration().get().getDeploymentName());
                  } else {
                     return Optional.<Deployment>empty();
                  }
               });

            return Operators.compose(propertiesCS, deploymentOptCS, (properties, deployment) -> {
               var pid = project.getId();
               Map<String, String> result = Maps.newHashMap();
               result.put("MQ_PROJECT", project.getId().getValue());

               if (deployment.isPresent() && properties.getMlflowConfiguration().isPresent()) {
                  var dep = deployment.get();
                  var config = properties.getMlflowConfiguration().get();

                  var mlflowPortsCS = dep
                     .getContainer(config.getMlflowContainerName(pid))
                     .map(Container::getMappedPortUrls)
                     .orElse(CompletableFuture.completedFuture(Maps.newHashMap()));

                  var minioPortsCS = dep
                     .getContainer(config.getMinioContainerName(pid))
                     .map(Container::getMappedPortUrls)
                     .orElse(CompletableFuture.completedFuture(Maps.newHashMap()));

                  return Operators.compose(mlflowPortsCS, minioPortsCS, (mlflowPorts, minioPorts) -> {
                     if (mlflowPorts.containsKey(5000)) {
                        result.put("MLFLOW_TRACKING_URI", mlflowPorts.get(5000).toString());
                     } else {
                        LOG.warn("No MLflow tracking URL found for project {}", pid);
                     }

                     if (minioPorts.containsKey(9000)) {
                        result.put("MLFLOW_S3_ENDPOINT_URL", minioPorts.get(9000).toString());
                     } else {
                        LOG.warn("No minio endpoint found for project {}", pid);
                     }

                     result.put("AWS_ACCESS_KEY_ID", config.getMinioAccessKey());
                     result.put("AWS_SECRET_ACCESS_KEY", config.getMinioSecretKey());
                     result.put("AWS_DEFAULT_REGION", "mzg");

                     return result;
                  });
               } else {
                  return CompletableFuture.completedFuture(result);
               }
            }).thenCompose(r -> r);
         });
   }

   @Override
   public CompletionStage<List<ProjectProperties>> list(User user) {
      return projects.getProjects();
   }

   @Override
   public CompletionStage<Project> get(User user, String name) {
      return projects
         .getProjectByName(name)
         .thenCompose(project -> {
            var propertiesCS = project.getProperties();
            var membersCS = project.members().getMembers();
            var sandboxesCS = sandboxes
               .listSandboxes(project.getId())
               .thenApply(sandboxes -> sandboxes
                  .stream()
                  .map(sandboxCompanion::enrichSandboxProperties))
               .thenCompose(Operators::allOf);

            var accessRequestsCS = Operators
               .compose(
                  datasets.findAccessRequestsByProject(project.getId()), propertiesCS,
                  (requests, properties) -> requests
                     .stream()
                     .map(request -> companion.enrichDataAccessRequest(
                        properties, request,
                        id -> datasets.getById(id).thenCompose(DatasetEntity::getProperties).thenApply(p -> p))))
               .thenCompose(Operators::allOf);

            var linkedDataAssetsCS = accessRequestsCS.thenApply(requests -> requests
               .stream()
               .map(r -> {
                  if (r.getAsset() instanceof DatasetProperties) {
                     return datasets
                        .getById(r.getAsset().getId())
                        .thenCompose(datasetCompanion::mapEntityToDataset);
                  } else if (r.getAsset() instanceof DataSourceProperties) {
                     return dataSources
                        .getById(r.getAsset().getId())
                        .thenCompose(dataSourceCompanion::mapEntityToDataSource);
                  } else {
                     return CompletableFuture.<Dataset>failedFuture(new IllegalArgumentException("Unknown data asset type."));
                  }
               })
               .map(cs -> cs.thenApply(as -> (DataAsset<?>) as)))
               .thenCompose(Operators::allOf);

            var stacks = Stacks.apply()
               .getStacks()
               .stream()
               .map(Stack::getProperties)
               .collect(Collectors.toList());

            return Operators.compose(
               propertiesCS, membersCS, accessRequestsCS, sandboxesCS, linkedDataAssetsCS,
               (properties, members, accessRequests, sandboxes, linkedDataAssets) -> {
                  var mlflowBaseUrl = properties
                     .getMlflowConfiguration()
                     .map(c -> c.getMlflowBasePath(project.getId()))
                     .orElse("/_mlflow/" + project.getId());

                  return Project.apply(
                     project.getId(), properties.getName(), properties.getTitle(), properties.getSummary(),
                     mlflowBaseUrl, properties.getCreated(), properties.getModified(), accessRequests, members,
                     linkedDataAssets.stream().map(as -> (DataAsset<?>) as).collect(Collectors.toList()),
                     sandboxes, stacks);
               });
         });
   }

   @Override
   public CompletionStage<Done> remove(User user, String name) {
      return projects
         .findProjectByName(name)
         .thenCompose(maybeProject -> {
            if (maybeProject.isPresent()) {
               var projectId = maybeProject.get().getId();

               return projects
                  .removeProject(projectId)
                  .thenCompose(done -> infrastructure.removeDeployment(String.format("mq__%s", projectId)));
            } else {
               return CompletableFuture.completedFuture(Done.getInstance());
            }
         });
   }

   @Override
   public CompletionStage<Done> update(User user, String name, String updatedName, String title, String summary) {
      return projects
         .getProjectByName(name)
         .thenCompose(project -> project.updateProperties(user, updatedName, title, summary));
   }

   /*
    * Manage members
    */

   @Override
   public CompletionStage<Done> grant(User user, String name, Authorization authorization, ProjectMemberRole role) {
      return projects
         .getProjectByName(name)
         .thenCompose(project -> project.members().addMember(user, authorization, role));
   }

   @Override
   public CompletionStage<Done> revoke(User user, String name, Authorization authorization) {
      return projects
         .getProjectByName(name)
         .thenCompose(project -> project.members().removeMember(user, authorization));
   }

   private static DeploymentConfig createMlflowDeploymentConfig(UID project, MlflowConfiguration properties) {
      var minioContainerCfg = ContainerConfig
         .builder(properties.getMinioContainerName(project), "mq-stacks--mlflow-minio:0.0.1")
         .withEnvironmentVariable("MINIO_ACCESS_KEY", properties.getMinioAccessKey())
         .withEnvironmentVariable("MINIO_SECRET_KEY", properties.getMinioSecretKey())
         .withEnvironmentVariable("MINIO_REGION_NAME", "mzg")
         .withPort(9000)
         .build();

      var postgresContainerCfg = ContainerConfig
         .builder(properties.getPostgreContainerName(project), "postgres:12.4")
         .withEnvironmentVariable("POSTGRES_USER", properties.getPostgresUsername())
         .withEnvironmentVariable("POSTGRES_PASSWORD", properties.getPostgresPassword())
         .withEnvironmentVariable("PGDATA", "/data")
         .withPort(5432)
         .build();

      var mlflowContainerCfg = ContainerConfig
         .builder(properties.getMlflowContainerName(project), "mq-stacks--mlflow-server:0.0.1")
         .withEnvironmentVariable(
            "MLFLOW_S3_ENDPOINT_URL",
            String.format("http://%s:9000", properties.getMinioContainerName(project)))
         .withEnvironmentVariable("AWS_ACCESS_KEY_ID", properties.getMinioAccessKey())
         .withEnvironmentVariable("AWS_SECRET_ACCESS_KEY", properties.getMinioSecretKey())
         .withEnvironmentVariable("AWS_DEFAULT_REGION", "mzg")
         .withEnvironmentVariable("POSTGRES_USERNAME", properties.getPostgresUsername())
         .withEnvironmentVariable("POSTGRES_PASSWORD", properties.getPostgresPassword())
         .withEnvironmentVariable("POSTGRES_HOST", properties.getPostgreContainerName(project))
         .withEnvironmentVariable("MLFLOW_PREFIX", properties.getMlflowBasePath(project))
         .withPort(5000)
         .build();

      return DeploymentConfig
         .builder(properties.getDeploymentName())
         .withContainerConfig(minioContainerCfg)
         .withContainerConfig(postgresContainerCfg)
         .withContainerConfig(mlflowContainerCfg)
         .build();
   }

}
