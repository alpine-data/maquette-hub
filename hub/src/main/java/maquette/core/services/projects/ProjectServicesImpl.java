package maquette.core.services.projects;

import akka.Done;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.DataAssetEntities;
import maquette.core.entities.infrastructure.Container;
import maquette.core.entities.infrastructure.Deployment;
import maquette.core.entities.infrastructure.InfrastructureManager;
import maquette.core.entities.infrastructure.model.ContainerConfig;
import maquette.core.entities.infrastructure.model.ContainerProperties;
import maquette.core.entities.infrastructure.model.DeploymentConfig;
import maquette.core.entities.infrastructure.model.Volume;
import maquette.core.entities.processes.ProcessManager;
import maquette.core.entities.projects.*;
import maquette.core.entities.projects.model.MlflowConfiguration;
import maquette.core.entities.projects.model.Project;
import maquette.core.entities.projects.model.ProjectMemberRole;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.entities.projects.model.apps.Application;
import maquette.core.entities.projects.model.model.Model;
import maquette.core.entities.projects.model.model.ModelMemberRole;
import maquette.core.entities.projects.model.model.ModelMembersCompanion;
import maquette.core.entities.projects.model.model.ModelProperties;
import maquette.core.entities.projects.model.model.events.Approved;
import maquette.core.entities.projects.model.model.events.Rejected;
import maquette.core.entities.projects.model.model.events.ReviewRequested;
import maquette.core.entities.projects.model.model.governance.CodeIssue;
import maquette.core.entities.projects.model.model.governance.CodeQuality;
import maquette.core.entities.sandboxes.SandboxEntities;
import maquette.core.entities.sandboxes.model.stacks.Stack;
import maquette.core.entities.sandboxes.model.stacks.Stacks;
import maquette.core.services.data.DataAssetCompanion;
import maquette.core.services.sandboxes.SandboxCompanion;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.time.Instant;
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

   SandboxEntities sandboxes;

   InfrastructureManager infrastructure;

   DataAssetEntities assets;

   DataAssetCompanion assetCompanion;

   ProjectCompanion companion;

   SandboxCompanion sandboxCompanion;

   public static ProjectServicesImpl apply(
      ProcessManager processes, ProjectEntities projects, SandboxEntities sandboxes,
      InfrastructureManager infrastructure, DataAssetEntities entities, DataAssetCompanion assetCompanion,
      ProjectCompanion companion, SandboxCompanion sandboxCompanion) {

      var impl = new ProjectServicesImpl(
         processes, projects, sandboxes, infrastructure,
         entities, assetCompanion, companion, sandboxCompanion);
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
                  assets.findAccessRequestsByProject(project.getId()), propertiesCS,
                  (requests, properties) -> requests
                     .stream()
                     .map(request -> companion.enrichDataAccessRequest(
                        properties, request,
                        id -> assets.getById(id).getProperties().thenApply(p -> p))))
               .thenCompose(Operators::allOf);

            var linkedDataAssetsCS = accessRequestsCS.thenApply(requests -> requests
               .stream()
               .map(r -> {
                  var assetEntity = assets.getById(r.getAsset().getId());
                  return assetCompanion.enrichDataAsset(assetEntity);
               }))
               .thenCompose(Operators::allOf);

            var stacks = Stacks.apply()
               .getStacks()
               .stream()
               .map(Stack::getProperties)
               .collect(Collectors.toList());

            var mlflowCS = propertiesCS
               .thenCompose(properties -> Operators.optCS(properties
                  .getMlflowConfiguration()
                  .flatMap(config -> infrastructure.findDeployment(config.getDeploymentName()))
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
                     linkedDataAssets, sandboxes, stacks, mlflow.orElse(null));
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
      var projectCS = projects
         .getProjectByName(project);

      var modelEntityCS = projectCS
         .thenCompose(ProjectEntity::getModels)
         .thenApply(models -> models.getModel(model));

      var projectMembersCS = projectCS
         .thenCompose(p -> p.members().getMembers());

      return Operators
         .compose(modelEntityCS, projectMembersCS, (modelEntity, projectMembers) -> {
            var propertiesCS = modelEntity.getProperties();
            var membersCS = modelEntity.getMembers();
            var permissionsCS = membersCS
               .thenApply(members -> ModelMembersCompanion.apply(members, projectMembers))
               .thenApply(comp -> comp.getDataAssetPermissions(user));

            return Operators.compose(propertiesCS, membersCS, permissionsCS, Model::fromProperties);
         })
         .thenCompose(cs -> cs);
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
   public CompletionStage<Done> updateModelVersion(User user, String project, String model, String version, String description) {
      return projects
         .getProjectByName(project)
         .thenCompose(ProjectEntity::getModels)
         .thenApply(models -> models.getModel(model))
         .thenCompose(m -> m.updateModelVersion(user, version, mdl -> mdl.withDescription(description)));
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
         .thenCompose(m -> m.updateModelVersion(
            user, version,
            v -> v.withEvent(Approved.apply(ActionMetadata.apply(user)))));
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
   public CompletionStage<Done> rejectModel(User user, String project, String model, String version, String reason) {
      return projects
         .getProjectByName(project)
         .thenCompose(ProjectEntity::getModels)
         .thenApply(models -> models.getModel(model))
         .thenCompose(m -> m.updateModelVersion(user, version, mdl -> mdl.withEvent(Rejected.apply(ActionMetadata.apply(user), reason))));
   }

   @Override
   public CompletionStage<Done> requestModelReview(User user, String project, String model, String version) {
      return projects
         .getProjectByName(project)
         .thenCompose(ProjectEntity::getModels)
         .thenApply(models -> models.getModel(model))
         .thenCompose(m -> m.updateModelVersion(user, version, mdl -> mdl.withEvent(ReviewRequested.apply(ActionMetadata.apply(user)))));
   }

   @Override
   public CompletionStage<Done> reportCodeQuality(User user, String project, String model, String version, String commit, int score, int coverage, List<CodeIssue> issues) {
      return projects
         .getProjectByName(project)
         .thenCompose(ProjectEntity::getModels)
         .thenApply(models -> models.getModel(model))
         .thenCompose(m -> m.updateModelVersion(user, version, mdl -> {
            var quality = CodeQuality.apply(Instant.now(), commit, score, coverage, issues);
            return mdl.withCodeQuality(quality);
         }));
   }

   @Override
   public CompletionStage<Done> runExplainer(User user, String project, String model, String version) {
      var projectEntityCS = projects.getProjectByName(project);
      var modelEntityCS = projectEntityCS.thenCompose(ProjectEntity::getModels).thenApply(models -> models.getModel(model));
      var modelPropertiesCS = modelEntityCS.thenCompose(ModelEntity::getProperties);

      return Operators.compose(
         projectEntityCS, modelEntityCS, modelPropertiesCS,
         (projectEntity, modelEntity, modelProperties) -> modelProperties
            .getVersion(version)
            .getExplainer()
            .map(explainer -> {
               var cfg = createExplainerDeploymentConfig(projectEntity.getId(), explainer.getFile());
               return infrastructure
                  .applyConfig(cfg)
                  .thenCompose(done -> infrastructure.getDeployment(cfg.getName()).getContainers().get(0).getMappedPortUrls())
                  .thenCompose(mappedPorts -> {
                     LOG.info("Started explainer runtime {} for {}/{}/{}", cfg.getName(), project, model, version);

                     return modelEntity.updateModelVersion(user, version, mdl -> {
                        var explainerUpdated = explainer.withExternalUrl(mappedPorts.get(8050).toString());
                        return mdl.withExplainer(explainerUpdated);
                     });
                  });
            }))
         .thenCompose(Operators::optCS)
         .thenApply(opt -> opt.orElse(Done.getInstance()));
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
            .findDeployment(mlflowConfig.getDeploymentName())
            .flatMap(deployment -> deployment.findContainer(mlflowConfig.getMlflowContainerName(projectProperties.getId())))
            .map(Container::getProperties)))
         .thenApply(properties -> properties.map(ContainerProperties::getMappedPortUrls))
         .thenApply(ports -> ports.orElse(Maps.newHashMap()))
         .thenCompose(mlflowPorts -> {
            var pid = projectProperties.getId();

            if (mlflowPorts.containsKey(5000) && projectProperties.getMlflowConfiguration().isPresent()) {
               var mlflowConfig = projectProperties.getMlflowConfiguration().get();
               var externalPort = mlflowPorts
                  .get(5000)
                  .toString();

               return infrastructure
                  .registerRoute(
                     pid.getValue(),
                     mlflowConfig.getMlflowBasePath(pid),
                     mlflowPorts.get(5000).toString().replace("localhost", "host.docker.internal"))
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

   private static DeploymentConfig createExplainerDeploymentConfig(UID project, Path explainerFile) {
      var name = String.format("mq--%s--xpl-%s", project.getValue(), UID.apply(8));
      var containerCfg = ContainerConfig
         .builder(name, "mq-services--shapash:0.0.1")
         .withEnvironmentVariable("MQ_XPL_PATH", "/opt/xpl/xpl.pkl")
         .withVolume(Volume.apply(explainerFile.getParent().toAbsolutePath(), "/opt/xpl"))
         .withPort(8050)
         .build();

      return DeploymentConfig
         .builder(name)
         .withContainerConfig(containerCfg)
         .build();
   }

}
