package maquette.core.entities.sandboxes.model.stacks;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.common.forms.Form;
import maquette.common.forms.FormControl;
import maquette.common.forms.FormRow;
import maquette.common.forms.inputs.Input;
import maquette.core.entities.infrastructure.model.DeploymentConfig;
import maquette.core.entities.infrastructure.model.DeploymentConfigs;
import maquette.core.entities.infrastructure.model.DeploymentProperties;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.entities.sandboxes.model.SandboxProperties;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class Neo4JStack implements Stack<Neo4JStack.Configuration> {

   public static final String STACK_NAME = "neo4j";

   @Override
   public String getTitle() {
      return "Neo4j";
   }

   @Override
   public String getName() {
      return STACK_NAME;
   }

   @Override
   public String getSummary() {
      return "Neo4j graph database instance";
   }

   @Override
   public String getIcon() {
      return STACK_NAME;
   }

   @Override
   public List<String> getTags() {
      return List.of("Storage", "Analytics");
   }

   @Override
   public Class<Configuration> getConfigurationType() {
      return Configuration.class;
   }

   @Override
   public Form getConfigurationForm() {
      var dbUsername = FormControl.apply(
         "Username",
         Input.apply("dbUsername"),
         "Leave empty for random value.");

      var dbPassword = FormControl.apply(
         "Password",
         Input.apply("dbPassword"),
         "Leave empty for random value.");

      return Form
         .apply()
         .withRow(FormRow
            .apply()
            .withFormControl(dbUsername)
            .withFormControl(dbPassword));
   }

   @Override
   public DeploymentConfig getDeploymentConfig(ProjectProperties project, SandboxProperties sandbox, Configuration properties, Map<String, String> projectEnvironment) {
      return DeploymentConfigs.sample(project.getId().getValue(), sandbox.getId().getValue());
   }

   @Override
   public CompletionStage<DeployedStackParameters> getParameters(DeploymentProperties deployment, Configuration configuration) {
      var launch_pgAdmin = DeployedStackParameters
         .apply("http://pgadmin.postgres.maquettte.local:3829", "Launch pgAdmin")
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
