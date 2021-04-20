package maquette.core.entities.projects.model.sandboxes.stacks;

import maquette.common.forms.Form;
import maquette.core.entities.infrastructure.model.DataVolume;
import maquette.core.entities.infrastructure.model.DeploymentConfig;
import maquette.core.entities.infrastructure.model.DeploymentProperties;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.entities.projects.model.sandboxes.SandboxProperties;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public interface Stack<T extends StackConfiguration> {

   String getTitle();

   String getName();

   String getSummary();

   String getIcon();

   List<String> getTags();

   Class<T> getConfigurationType();

   Form getConfigurationForm();

   DeploymentConfig getDeploymentConfig(
      ProjectProperties project, SandboxProperties sandbox, DataVolume volume,
      T properties, Map<String, String> projectEnvironment);

   CompletionStage<DeployedStackParameters> getParameters(DeploymentProperties deployment, T configuration);

   default StackProperties getProperties() {
      return StackProperties.apply(getTitle(), getName(), getSummary(), getIcon(), getTags(), getConfigurationForm());
   }

}
