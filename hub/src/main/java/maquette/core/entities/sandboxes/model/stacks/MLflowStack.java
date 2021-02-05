package maquette.core.entities.sandboxes.model.stacks;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.common.forms.Form;
import maquette.common.forms.FormControl;
import maquette.common.forms.FormRow;
import maquette.common.forms.inputs.Input;
import maquette.core.entities.infrastructure.model.ContainerConfig;
import maquette.core.entities.infrastructure.model.DeploymentConfig;
import maquette.core.entities.infrastructure.model.DeploymentProperties;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.entities.sandboxes.model.SandboxProperties;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class MLflowStack implements Stack<MLflowStack.Configuration> {

   public static final String STACK_NAME = "postgresql";

   @Override
   public String getTitle() {
      return "PostgreSQL with pgAdmin";
   }

   @Override
   public String getName() {
      return STACK_NAME;
   }

   @Override
   public String getSummary() {
      return "PostgreSQL RDBMS with PostgreSQL";
   }

   @Override
   public String getIcon() {
      return STACK_NAME;
   }

   @Override
   public List<String> getTags() {
      return List.of("Storage");
   }

   @Override
   public Class<Configuration> getConfigurationType() {
      return Configuration.class;
   }

   @Override
   public Form getConfigurationForm() {
      var dbUsername = FormControl.apply(
         "Database username",
         Input.apply("dbUsername"),
         "The username for the database. Leave empty for random value.");

      var dbPassword = FormControl.apply(
         "Database password",
         Input.apply("dbPassword"),
         "The password for the database. Leave empty for random value.");

      var adminMail = FormControl.apply(
         "pgAdmin E-Mail address",
         Input.apply("pgAdminMail", "dev@maquette.ai"));

      var adminPassword = FormControl.apply(
         "pgAdmin password",
         Input.apply("pgAdminPassword"),
         "Password for accessing pgAdmin. Leave empty for random value.");

      return Form
         .apply()
         .withRow(FormRow
            .apply()
            .withFormControl(dbUsername)
            .withFormControl(dbPassword))
         .withRow(FormRow
            .apply()
            .withFormControl(adminMail)
            .withFormControl(adminPassword));
   }

   @Override
   public DeploymentConfig getDeploymentConfig(ProjectProperties project, SandboxProperties sandbox, Configuration properties) {
      var minioContainerName =String.format("mq__%s__postgres", project.getId());
      var postgresContainerName = String.format("mq__%s__postgres", project.getId());
      var mlflowContainerName = String.format("mq__%s__mlflow", project.getId());

      var minioContainerCfg = ContainerConfig
         .builder(minioContainerName, "mq-stacks--mlflow-minio:0.0.1")
         .withEnvironmentVariable("MINIO_ACCESS_KEY", properties.getMinioAccessKey())
         .withEnvironmentVariable("MINIO_SECRET_KEY", properties.getMinioSecretKey())
         .withEnvironmentVariable("MINIO_REGION_NAME", "mzg")
         .withPort(9000)
         .build();

      var postgresContainerCfg = ContainerConfig
         .builder(postgresContainerName, "postgres:12.4")
         .withEnvironmentVariable("POSTGRES_USER", properties.getPostgresUsername())
         .withEnvironmentVariable("POSTGRES_PASSWORD", properties.getPostgresPassword())
         .withEnvironmentVariable("PGDATA", "/data")
         .withPort(5432)
         .build();

      var mlflowContainerCfg = ContainerConfig
         .builder(mlflowContainerName, "mq-stacks--mlflow-server:0.0.1")
         .withEnvironmentVariable("MLFLOW_S3_ENDPOINT_URL", String.format("http://%s:9000", minioContainerName))
         .withEnvironmentVariable("AWS_ACCESS_KEY_ID", properties.getMinioAccessKey())
         .withEnvironmentVariable("AWS_SECRET_ACCESS_KEY", properties.getMinioSecretKey())
         .withEnvironmentVariable("AWS_DEFAULT_REGION", "mzg")
         .withPort(5000)
         .withCommand(String.format(
            "mlflow server --backend-store-uri postgresql://%s:%s@%s:5432/postgres --default-artifact-root s3://mlflow/ --host 0.0.0.0 --static-prefix /_mlflow/%s",
            postgresContainerName, properties.getPostgresUsername(), properties.getPostgresPassword(), project.getId()))
         .build();

      return DeploymentConfig
         .builder(String.format("mq__%s", project.getId()))
         .withContainerConfig(minioContainerCfg)
         .withContainerConfig(postgresContainerCfg)
         .withContainerConfig(mlflowContainerCfg)
         .build();
   }

   @Override
   public CompletionStage<DeployedStackParameters> getParameters(DeploymentProperties deployment, Configuration configuration) {
      var launch_pgAdmin = DeployedStackParameters
         .apply(deployment.getProperties().get(1).getMappedPortUrls().get(80).toString().replace("localhost", "hub.maquette.ai.internal"), "Launch pgAdmin");
         /*
         .withParameter("Database Username", configuration.dbUsername)
         .withParameter("Database Password", configuration.dbPassword)
         .withParameter("pgAdmin Login", configuration.pgAdminMail)
         .withParameter("pgAdmin Password", configuration.pgAdminPassword);
          */

      return CompletableFuture.completedFuture(launch_pgAdmin);
   }

   @Value
   @AllArgsConstructor(staticName = "apply")
   @NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
   public static class Configuration implements StackConfiguration {

      String minioAccessKey;

      String minioSecretKey;

      String postgresPassword;

      String postgresUsername;

      @Override
      @JsonIgnore
      public String getStackName() {
         return STACK_NAME;
      }

   }

}
