package maquette.core.services.projects;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.entities.data.datasets.DatasetEntities;
import maquette.core.entities.infrastructure.model.ContainerConfig;
import maquette.core.entities.infrastructure.model.DeploymentConfig;
import maquette.core.entities.projects.ProjectEntities;
import maquette.core.entities.projects.model.ProjectMemberRole;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.services.ServiceCompanion;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.access.DataAccessRequestProperties;
import maquette.core.values.data.DataAssetProperties;
import maquette.core.values.user.User;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

@AllArgsConstructor(staticName = "apply")
public final class ProjectCompanion extends ServiceCompanion {

   ProjectEntities projects;

   DatasetEntities datasets;

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
