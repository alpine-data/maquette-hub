package maquette.core.entities.sandboxes.model.stacks;

import maquette.common.forms.Form;
import maquette.core.entities.infrastructure.model.DeploymentConfig;
import maquette.core.entities.infrastructure.model.DeploymentProperties;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.entities.sandboxes.model.SandboxProperties;

import java.util.List;

public interface Stack<T extends StackConfiguration> {

   String getTitle();

   String getName();

   String getSummary();

   String getIcon();

   List<String> getTags();

   Class<T> getParametersType();

   Form getConfigurationForm();

   DeploymentConfig getDeploymentConfig(ProjectProperties project, SandboxProperties sandbox, T properties);

   DeployedStackParameters getProperties(DeploymentProperties deployment);

}
