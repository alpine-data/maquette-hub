package maquette.core.services.projects;

import akka.Done;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.model.DataAssetProperties;
import maquette.core.entities.infrastructure.Container;
import maquette.core.entities.infrastructure.Deployment;
import maquette.core.entities.infrastructure.InfrastructureManager;
import maquette.core.entities.infrastructure.model.ContainerConfig;
import maquette.core.entities.infrastructure.model.DeploymentConfig;
import maquette.core.entities.projects.ProjectEntities;
import maquette.core.entities.projects.model.ProjectMemberRole;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.services.ServiceCompanion;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.access.DataAccessRequestProperties;
import maquette.core.values.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

@AllArgsConstructor(staticName = "apply")
public final class ProjectCompanion extends ServiceCompanion {

   private static final Logger LOG = LoggerFactory.getLogger(ProjectServices.class);

   ProjectEntities projects;

   InfrastructureManager infrastructure;

   public DeploymentConfig createProjectBaseDeployment(String projectId) {
      var postgresContainerCfg = ContainerConfig
         .builder(String.format("mq__%s__psql", projectId), "postgres:12.4")
         .withEnvironmentVariable("POSTGRES_USER", "postgres")
         .withEnvironmentVariable("POSTGRES_PASSWORD", "password")
         .withEnvironmentVariable("PGDATA", "/data")
         .withPort(5432)
         .build();

      var minioContainerCfg = ContainerConfig
         .builder(String.format("mq__%s__minio", projectId), "minio/minio:latest")
         .withEnvironmentVariable("MINIO_ACCESS_KEY", "maquette")
         .withEnvironmentVariable("MINIO_SECRET_KEY", "password")
         .withPort(9000)
         .withCommand("server /data")
         .build();

      return DeploymentConfig
         .builder(String.format("mq__%s", projectId))
         .withContainerConfig(postgresContainerCfg)
         .withContainerConfig(minioContainerCfg)
         .build();
   }

   public CompletionStage<DataAccessRequest> enrichDataAccessRequest(ProjectProperties project, DataAccessRequestProperties request, Function<UID, CompletionStage<DataAssetProperties>> findAsset) {
      return findAsset
         .apply(request.getAsset())
         .thenApply(asset -> DataAccessRequest.apply(request.getId(), request.getCreated(), asset, project, request.getEvents()));
   }

   public CompletionStage<Map<String, String>> environment(String name, EnvironmentType type) {
      return projects
         .getProjectByName(name)
         .thenCompose(project -> {
            var propertiesCS = project.getProperties();
            var deploymentOptCS = propertiesCS
               .thenApply(properties -> {
                  if (properties.getMlflowConfiguration().isPresent()) {
                     return infrastructure
                        .findDeployment(properties.getMlflowConfiguration().get().getDeploymentName());
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
                     .findContainer(config.getMlflowContainerName(pid))
                     .map(Container::getMappedPortUrls)
                     .orElse(CompletableFuture.completedFuture(Maps.newHashMap()));

                  var minioPortsCS = dep
                     .findContainer(config.getMinioContainerName(pid))
                     .map(Container::getMappedPortUrls)
                     .orElse(CompletableFuture.completedFuture(Maps.newHashMap()));

                  return Operators.compose(mlflowPortsCS, minioPortsCS, (mlflowPorts, minioPorts) -> {
                     if (mlflowPorts.containsKey(5000)) {
                        switch (type) {
                           case EXTERNAL:
                              result.put("MLFLOW_TRACKING_URI", mlflowPorts.get(5000).toString());
                              break;
                           case SANDBOX:
                              var url = mlflowPorts.get(5000).toString().replace("localhost", "host.docker.internal");
                              result.put("MLFLOW_TRACKING_URI", url);
                              break;
                        }
                     } else {
                        LOG.warn("No MLflow tracking URL found for project {}", pid);
                     }

                     if (minioPorts.containsKey(9000)) {
                        switch (type) {
                           case EXTERNAL:
                              result.put("MLFLOW_S3_ENDPOINT_URL", minioPorts.get(9000).toString());
                              break;
                           case SANDBOX:
                              var url = minioPorts.get(9000).toString().replace("localhost", "host.docker.internal");
                              result.put("MLFLOW_S3_ENDPOINT_URL", url);
                              break;
                        }
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

   public <T> CompletionStage<Optional<T>> filterMember(User user, String name, T passThrough) {
      return filterMember(user, name, null, passThrough);
   }

   public <T> CompletionStage<Optional<T>> filterMember(User user, String name, ProjectMemberRole role, T passThrough) {
      return projects.getProjectByName(name)
         .thenCompose(project -> project.isMember(user, role))
         .thenApply(auth -> {
            if (auth) {
               return Optional.of(passThrough);
            } else {
               return Optional.empty();
            }
         });
   }

   public CompletionStage<Boolean> isMember(User user, String name) {
      return isMember(user, name, null);
   }

   public CompletionStage<Boolean> isMember(User user, String name, ProjectMemberRole role) {
      return filterMember(user, name, role, Done.getInstance()).thenApply(Optional::isPresent);
   }

}
