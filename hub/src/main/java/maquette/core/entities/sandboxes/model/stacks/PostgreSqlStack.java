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

@AllArgsConstructor(staticName = "apply")
public final class PostgreSqlStack implements Stack<PostgreSqlStack.Configuration> {

   private static final String STACK_NAME = "postgresql";

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
   public Class<Configuration> getParametersType() {
      return Configuration.class;
   }

   @Override
   public Form getConfigurationForm() {
      var dbUsername = FormControl.apply(
         "Database Username",
         Input.apply("dbUsername"),
         "The username for the database. Leave empty for random value.");

      var dbPassword = FormControl.apply(
         "Database Password",
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
            .apply(true)
            .withFormControl(dbUsername)
            .withFormControl(dbPassword))
         .withRow(FormRow
            .apply()
            .withFormControl(adminMail)
            .withFormControl(adminPassword));
   }

   @Override
   public DeploymentConfig getDeploymentConfig(ProjectProperties project, SandboxProperties sandbox, Configuration properties) {
      return DeploymentConfigs.sample(project.getId(), sandbox.getId());
   }

   @Override
   public StackProperties getProperties(DeploymentProperties deployment) {
      return StackProperties.apply("http://pgadmin.postgres.maquettte.local:3829");
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
