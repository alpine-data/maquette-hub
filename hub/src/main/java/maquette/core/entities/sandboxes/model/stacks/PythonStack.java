package maquette.core.entities.sandboxes.model.stacks;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.common.forms.Form;
import maquette.common.forms.FormControl;
import maquette.common.forms.inputs.InputPicker;
import maquette.core.entities.infrastructure.model.DeploymentConfig;
import maquette.core.entities.infrastructure.model.DeploymentConfigs;
import maquette.core.entities.infrastructure.model.DeploymentProperties;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.entities.sandboxes.model.SandboxProperties;

import java.net.URL;
import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class PythonStack implements Stack<PythonStack.PythonStackConfiguration> {

   private static final String STACK_NAME = "python";

   @Override
   public String getTitle() {
      return "Python";
   }

   @Override
   public String getName() {
      return STACK_NAME;
   }

   @Override
   public String getSummary() {
      return "A default python stack with Python Kernel, Miniconda and Jupyter.";
   }

   @Override
   public String getIcon() {
      return STACK_NAME;
   }

   @Override
   public List<String> getTags() {
      return List.of("python", "notebook");
   }

   @Override
   public Class<PythonStackConfiguration> getParametersType() {
      return PythonStackConfiguration.class;
   }

   @Override
   public Form getConfigurationForm() {
      var version = FormControl
         .apply(
            "Python Version",
            InputPicker
               .apply("version", "3.8")
               .withItem("3.8", "Python 3.8")
               .withItem("3.7", "Python 3.7"))
         .withHelpText("Select the Python version you prefer.");

      return Form.apply().withControl(version);
   }

   @Override
   public DeploymentConfig getDeploymentConfig(ProjectProperties project, SandboxProperties sandbox, PythonStackConfiguration properties) {
      return DeploymentConfigs.sample(project.getId(), sandbox.getId());
   }

   @Override
   public StackProperties getProperties(DeploymentProperties deployment) {
      return StackProperties.apply(Operators.suppressExceptions(() -> new URL("http://pathtojupyterhub.com")));
   }

   @Value
   @AllArgsConstructor(staticName = "apply")
   @NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
   public static class PythonStackConfiguration implements StackConfiguration {

      String version;

      @Override
      @JsonIgnore
      public String getStackName() {
         return STACK_NAME;
      }
   }

}
