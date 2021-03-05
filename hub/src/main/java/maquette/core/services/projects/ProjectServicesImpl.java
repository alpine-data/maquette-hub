package maquette.core.services.projects;

import akka.Done;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
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
import maquette.core.entities.infrastructure.model.ContainerProperties;
import maquette.core.entities.infrastructure.model.DeploymentConfig;
import maquette.core.entities.processes.ProcessManager;
import maquette.core.entities.projects.*;
import maquette.core.entities.projects.model.*;
import maquette.core.entities.projects.model.apps.Application;
import maquette.core.entities.projects.model.model.Model;
import maquette.core.entities.projects.model.model.ModelProperties;
import maquette.core.entities.projects.model.model.ModelMemberRole;
import maquette.core.entities.sandboxes.SandboxEntities;
import maquette.core.entities.sandboxes.model.stacks.Stack;
import maquette.core.entities.sandboxes.model.stacks.Stacks;
import maquette.core.services.data.datasets.DatasetCompanion;
import maquette.core.services.data.datasources.DataSourceCompanion;
import maquette.core.services.sandboxes.SandboxCompanion;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.UserAuthorization;
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

@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

   public static ProjectServicesImpl apply(
      ProcessManager processes, ProjectEntities projects, DatasetEntities datasets, DataSourceEntities dataSources,
      SandboxEntities sandboxes, InfrastructureManager infrastructure, ProjectCompanion companion,
      DatasetCompanion datasetCompanion, DataSourceCompanion dataSourceCompanion, SandboxCompanion sandboxCompanion) {

      var impl = new ProjectServicesImpl(
         processes, projects, datasets, dataSources, sandboxes,
         infrastructure, companion, datasetCompanion, dataSourceCompanion, sandboxCompanion);

      impl.initialize();

      return impl;
   }

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
         .thenCompose(this::linkToolchainProxy);
   }

   @Override
   public CompletionStage<Map<String, String>> environment(User user, String name, EnvironmentType type) {
      return companion.environment(name, type);
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

            var mlflowCS = propertiesCS
               .thenCompose(properties -> Operators.optCS(properties
                  .getMlflowConfiguration()
                  .flatMap(config -> infrastructure.getDeployment(config.getDeploymentName()))
                  .map(Deployment::getProperties)));

            return Operators.compose(
               propertiesCS, membersCS, accessRequestsCS, sandboxesCS, linkedDataAssetsCS, mlflowCS,
               (properties, members, accessRequests, sandboxes, linkedDataAssets, mlflow) -> {
                  var mlflowBaseUrl = properties
                     .getMlflowConfiguration()
                     .map(c -> c.getMlflowBasePath(project.getId()))
                     .orElse("/_mlflow/" + project.getId());

                  return Project.apply(
                     project.getId(), properties.getName(), properties.getTitle(), properties.getSummary(),
                     mlflowBaseUrl, properties.getCreated(), properties.getModified(), accessRequests, members,
                     linkedDataAssets.stream().map(as -> (DataAsset<?>) as).collect(Collectors.toList()),
                     sandboxes, stacks, mlflow.orElse(null));
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
    * Model management
    */

   @Override
   public CompletionStage<List<ModelProperties>> getModels(User user, String name) {
      return projects
         .getProjectByName(name)
         .thenCompose(ProjectEntity::getModels)
         .thenCompose(ModelEntities::getModels);
   }

   @Override
   public CompletionStage<Model> getModel(User user, String project, String model) {
      return projects
         .getProjectByName(project)
         .thenCompose(ProjectEntity::getModels)
         .thenApply(models -> models.getModel(model))
         .thenCompose(entity -> {
            var propertiesCS = entity.getProperties();
            var membersCS = entity.getMembers();

            return Operators.compose(propertiesCS, membersCS, Model::fromProperties);
         });
   }

   @Override
   public CompletionStage<Done> updateModel(User user, String project, String model, String title, String description) {
      return projects
         .getProjectByName(project)
         .thenCompose(ProjectEntity::getModels)
         .thenApply(models -> models.getModel(model))
         .thenCompose(m -> m.updateModel(user, title, description));
   }

   @Override
   public CompletionStage<Done> answerQuestionnaire(User user, String project, String model, String version, JsonNode responses) {
      return projects
         .getProjectByName(project)
         .thenCompose(ProjectEntity::getModels)
         .thenApply(models -> models.getModel(model))
         .thenCompose(m -> m.answerQuestionnaire(user, version, responses));
   }

   @Override
   public CompletionStage<Done> approveModel(User user, String project, String model, String version) {
      return projects
         .getProjectByName(project)
         .thenCompose(ProjectEntity::getModels)
         .thenApply(models -> models.getModel(model))
         .thenCompose(m -> m.approveModel(user, version));
   }

   @Override
   public CompletionStage<Done> promoteModel(User user, String project, String model, String version, String stage) {
      return projects
         .getProjectByName(project)
         .thenCompose(ProjectEntity::getModels)
         .thenApply(models -> models.getModel(model))
         .thenCompose(m -> m.promoteModel(user, version, stage));
   }

   @Override
   public CompletionStage<Optional<JsonNode>> getLatestQuestionnaireAnswers(User user, String project, String model) {
      return projects
         .getProjectByName(project)
         .thenCompose(ProjectEntity::getModels)
         .thenApply(models -> models.getModel(model))
         .thenCompose(ModelEntity::getLatestQuestionnaireAnswers);
   }

   /*
    * Manage model members
    */

   @Override
   public CompletionStage<Done> grantModelRole(User user, String project, String model, UserAuthorization authorization, ModelMemberRole role) {
      // TODO mw: Check membership of project

      return projects
         .getProjectByName(project)
         .thenCompose(ProjectEntity::getModels)
         .thenApply(models -> models.getModel(model))
         .thenCompose(m -> m.addMember(user, authorization, role));
   }

   @Override
   public CompletionStage<Done> revokeModelRole(User user, String project, String model, UserAuthorization authorization) {
      return projects
         .getProjectByName(project)
         .thenCompose(ProjectEntity::getModels)
         .thenApply(models -> models.getModel(model))
         .thenCompose(m -> m.removeMember(user, authorization));
   }

   @Override
   public CompletionStage<Done> createApplication(User user, String project, String name, String description, String gitRepository) {
      return projects
         .getProjectByName(project)
         .thenCompose(ProjectEntity::getApplications)
         .thenCompose(apps -> apps.createApplication(user, name, description, gitRepository))
         .thenApply(i -> Done.getInstance());
   }

   @Override
   public CompletionStage<List<Application>> getApplications(User user, String project) {
      return projects
         .getProjectByName(project)
         .thenCompose(ProjectEntity::getApplications)
         .thenCompose(ApplicationEntities::listApplications);
   }

   @Override
   public CompletionStage<Done> removeApplication(User user, String project, String name) {
      return projects
         .getProjectByName(project)
         .thenCompose(ProjectEntity::getApplications)
         .thenCompose(apps -> apps.getApplicationByName(name))
         .thenCompose(app -> app.remove(user));
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

   private void initialize() {
      projects
         .getProjects()
         .thenApply(projects -> projects
            .stream()
            .map(this::linkToolchainProxy))
         .thenCompose(Operators::allOf)
         .thenRun(() -> LOG.info("Initialized projects"));
   }

   private CompletionStage<Done> linkToolchainProxy(ProjectProperties projectProperties) {
      return Operators.optCS(projectProperties
         .getMlflowConfiguration()
         .flatMap(mlflowConfig -> infrastructure
            .getDeployment(mlflowConfig.getDeploymentName())
            .flatMap(deployment -> deployment.getContainer(mlflowConfig.getMlflowContainerName(projectProperties.getId())))
            .map(Container::getProperties)))
         .thenApply(properties -> properties.map(ContainerProperties::getMappedPortUrls))
         .thenApply(ports -> ports.orElse(Maps.newHashMap()))
         .thenCompose(mlflowPorts -> {
            var pid = projectProperties.getId();

            if (mlflowPorts.containsKey(5000) && projectProperties.getMlflowConfiguration().isPresent()) {
               var mlflowConfig = projectProperties.getMlflowConfiguration().get();
               var externalPort = mlflowPorts.get(5000).toString();

               return infrastructure
                  .registerRoute(
                     pid.getValue(),
                     mlflowConfig.getMlflowBasePath(pid),
                     mlflowPorts.get(5000).toString())
                  .thenApply(done -> {
                     LOG.info("Configure MLFlow proxy for project `{}`", projectProperties.getName());
                     return done;
                  })
                  .thenCompose(done -> projects
                     .getProjectById(projectProperties.getId())
                     .thenCompose(p -> p.setMlflowConfiguration(mlflowConfig.withTrackingUrl(externalPort))));
            } else {
               LOG.warn("Unable to register MLflow routes for project `{}` - Missing MLflow port information.", projectProperties.getName());
               return CompletableFuture.completedFuture(Done.getInstance());
            }
         })
         .exceptionally(e -> {
            LOG.warn("Unable to register MLflow routes for project `{}` - Missing MLflow port information.", projectProperties.getName());
            return Done.getInstance();
         });
   }

   private static DeploymentConfig createMlflowDeploymentConfig(UID project, MlflowConfiguration properties) {
      var minioContainerCfg = ContainerConfig
         .builder(properties.getMinioContainerName(project), "mq-stacks--mlflow-minio:0.0.1")
         .withEnvironmentVariable("MINIO_ACCESS_KEY", properties.getMinioAccessKey())
         .withEnvironmentVariable("MINIO_SECRET_KEY", properties.getMinioSecretKey())
         .withEnvironmentVariable("MINIO_REGION_NAME", "mzg")
         .withHostName("minio")
         .withNetwork(properties.getSandboxNetworkName(project))
         .withPort(9000)
         .build();

      var postgresContainerCfg = ContainerConfig
         .builder(properties.getPostgreContainerName(project), "postgres:12.4")
         .withEnvironmentVariable("POSTGRES_USER", properties.getPostgresUsername())
         .withEnvironmentVariable("POSTGRES_PASSWORD", properties.getPostgresPassword())
         .withEnvironmentVariable("PGDATA", "/data")
         .withHostName("postgres")
         .withPort(5432)
         .build();

      var mlflowContainerCfg = ContainerConfig
         .builder(properties.getMlflowContainerName(project), "mq-stacks--mlflow-server:0.0.1")
         .withEnvironmentVariable("MLFLOW_S3_ENDPOINT_URL", "http://minio:9000")
         .withEnvironmentVariable("AWS_ACCESS_KEY_ID", properties.getMinioAccessKey())
         .withEnvironmentVariable("AWS_SECRET_ACCESS_KEY", properties.getMinioSecretKey())
         .withEnvironmentVariable("AWS_DEFAULT_REGION", "mzg")
         .withEnvironmentVariable("POSTGRES_USERNAME", properties.getPostgresUsername())
         .withEnvironmentVariable("POSTGRES_PASSWORD", properties.getPostgresPassword())
         .withEnvironmentVariable("POSTGRES_HOST", "postgres")
         .withEnvironmentVariable("MLFLOW_PREFIX", properties.getMlflowBasePath(project))
         .withHostName("mlflow")
         .withNetwork(properties.getSandboxNetworkName(project))
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
