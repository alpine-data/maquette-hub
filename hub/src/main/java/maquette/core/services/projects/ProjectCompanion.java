package maquette.core.services.projects;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.datasets.Dataset;
import maquette.core.entities.data.datasets.Datasets;
import maquette.core.entities.infrastructure.model.ContainerConfig;
import maquette.core.entities.infrastructure.model.DeploymentConfig;
import maquette.core.entities.projects.Project;
import maquette.core.entities.projects.Projects;
import maquette.core.services.ServiceCompanion;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.access.DataAccessRequestDetails;
import maquette.core.values.exceptions.NotAuthorizedException;
import maquette.core.values.exceptions.ProjectNotFoundException;
import maquette.core.values.user.User;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

@AllArgsConstructor(staticName = "apply")
public final class ProjectCompanion extends ServiceCompanion {

   Projects projects;

   Datasets datasets;

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

   public CompletionStage<Project> withProject(String name) {
      return projects
         .findProjectByName(name)
         .thenApply(maybeProject -> {
            if (maybeProject.isPresent()) {
               return maybeProject.get();
            } else {
               throw ProjectNotFoundException.applyFromName(name);
            }
         });
   }

   public CompletionStage<Optional<DataAccessRequestDetails>> mapDataAccessRequestToDetails(Project originProject, DataAccessRequest request) {
      var projectPropertiesCS = projects
         .getProjectById(request.getTargetProjectId())
         .thenCompose(Project::getProperties);
      var datasetPropertiesCS = datasets
         .getDatasetById(request.getTargetProjectId(), request.getTargetId())
         .thenCompose(Dataset::getProperties);
      var originPropertiesCS = originProject.getProperties();

      return Operators.compose(
         projectPropertiesCS, datasetPropertiesCS, originPropertiesCS,
         (targetProjectProperties, targetDatasetProperties, originProperties) -> {
            var details = DataAccessRequestDetails.apply(
               request.getId(),
               request.getCreated(),
               targetProjectProperties,
               targetDatasetProperties,
               originProperties,
               request.getEvents(),
               request.getStatus(),
               true,
               true);

            return Optional.of(details);
         });
   }

   public <T> CompletionStage<Optional<T>> filterMember(User user, String name, T passThrough) {
      return withProject(name)
         .thenCompose(Project::getDetails)
         .thenApply(details -> details.isMember(user))
         .thenApply(auth -> {
            if (auth) {
               return Optional.of(passThrough);
            } else {
               return Optional.empty();
            }
         });
   }

   public CompletionStage<Boolean> isMember(User user, String name) {
      return filterMember(user, name, Done.getInstance()).thenApply(Optional::isPresent);
   }

   public <T> CompletionStage<T> withMembership(User user, String name, Supplier<CompletionStage<T>> action) {
      return withMembership(user, name, action, String.format("You are not member of the project `%s`", name));
   }

   public <T> CompletionStage<T> withMembership(User user, String name, Supplier<CompletionStage<T>> action, String notAuthorizedMessage) {
      return isMember(user, name)
         .thenCompose(isMember -> {
            if (isMember) {
               return action.get();
            } else {
               throw NotAuthorizedException.apply(notAuthorizedMessage);
            }
         });
   }

}
