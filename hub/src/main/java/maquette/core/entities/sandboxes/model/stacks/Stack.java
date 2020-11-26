package maquette.core.entities.sandboxes.model.stacks;

import maquette.common.forms.Form;
import maquette.core.entities.infrastructure.model.DeploymentConfig;
import maquette.core.entities.infrastructure.model.DeploymentProperties;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.entities.sandboxes.model.SandboxProperties;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface Stack<T extends StackConfiguration> {

   String getTitle();

   String getName();

   String getSummary();

   String getIcon();

   List<String> getTags();

   Class<T> getConfigurationType();

   Form getConfigurationForm();

   DeploymentConfig getDeploymentConfig(ProjectProperties project, SandboxProperties sandbox, T properties);

   CompletionStage<DeployedStackParameters> getParameters(DeploymentProperties deployment, T configuration);

   default StackProperties getProperties() {
      return StackProperties.apply(getTitle(), getName(), getSummary(), getIcon(), getTags(), getConfigurationForm());
   }

}