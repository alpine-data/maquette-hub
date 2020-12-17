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
public final class PostgreSqlStack implements Stack<PostgreSqlStack.Configuration> {

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
      var postgresContainerCfg = ContainerConfig
         .builder(String.format("mq__%s_%s__psql", project.getId(), sandbox.getId()), "postgres:12.4")
         .withEnvironmentVariable("POSTGRES_USER", Operators.randomHash())
         .withEnvironmentVariable("POSTGRES_PASSWORD", Operators.randomHash())
         .withEnvironmentVariable("PGDATA", "/data")
         .withPort(5432)
         .build();

      var pgAdminConfig = ContainerConfig
         .builder(String.format("mq__%s_%s__pgadmin", project.getId(), sandbox.getId()), "dpage/pgadmin4:latest")
         .withEnvironmentVariable("PGADMIN_DEFAULT_EMAIL", properties.getPgAdminMail())
         .withEnvironmentVariable("PGADMIN_DEFAULT_PASSWORD", properties.getPgAdminPassword().length() > 0 ? properties.getPgAdminPassword() : Operators.randomHash())
         .withPort(80)
         .withPort(443)
         .withCommand("server /data")
         .build();

      System.out.println();

      return DeploymentConfig
         .builder(String.format("mq__%s", project.getId()))
         .withContainerConfig(postgresContainerCfg)
         .withContainerConfig(pgAdminConfig)
         .build();
   }

   @Override
   public CompletionStage<DeployedStackParameters> getParameters(DeploymentProperties deployment, Configuration configuration) {
      var launch_pgAdmin = DeployedStackParameters
         .apply(deployment.getProperties().get(1).getMappedPortUrls().get(80).toString().replace("localhost", "hub.maquette.ai.internal"), "Launch pgAdmin")
         .withParameter("Database Username", configuration.dbUsername)
         .withParameter("Database Password", configuration.dbPassword)
         .withParameter("pgAdmin Login", configuration.pgAdminMail)
         .withParameter("pgAdmin Password", configuration.pgAdminPassword);

      return CompletableFuture.completedFuture(launch_pgAdmin);
   }

   @Value
   @AllArgsConstructor(staticName = "apply")
   @NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
   public static class Configuration implements StackConfiguration {

      String dbUsername;

      String dbPassword;

      String pgAdminMail;

      String pgAdminPassword;

      @Override
      @JsonIgnore
      public String getStackName() {
         return STACK_NAME;
      }
   }

}
