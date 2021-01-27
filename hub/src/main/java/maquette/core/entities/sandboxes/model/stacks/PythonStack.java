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
import maquette.core.entities.infrastructure.model.ContainerConfig;
import maquette.core.entities.infrastructure.model.DeploymentConfig;
import maquette.core.entities.infrastructure.model.DeploymentProperties;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.entities.sandboxes.model.SandboxProperties;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
public class PythonStack implements Stack<PythonStack.Configuration> {

   public static final String STACK_NAME = "python";

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
   public Class<Configuration> getConfigurationType() {
      return Configuration.class;
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
   public DeploymentConfig getDeploymentConfig(ProjectProperties project, SandboxProperties sandbox, Configuration properties) {
      var postgresContainerCfg = ContainerConfig
         .builder(String.format("mq__%s_%s__jupyter", project.getId(), sandbox.getId()), "mq-stacks--python:0.0.1")
         .withEnvironmentVariable("MQ_USERNAME", sandbox.getCreated().getBy())
         .withEnvironmentVariable("MQ_AUTH_USERNAME", sandbox.getCreated().getBy())
         .withEnvironmentVariable("MQ_JUPYTER_TOKEN", Operators.randomHash())
         .withEnvironmentVariable("MQ_PROJECT", project.getId().toString())
         .withPort(8888)
         .withPort(9085)
         .build();

      return DeploymentConfig
         .builder(String.format("mq__%s_%s", project.getId(), sandbox.getId()))
         .withContainerConfig(postgresContainerCfg)
         .build();
   }

   @Override
   public CompletionStage<DeployedStackParameters> getParameters(DeploymentProperties deployment, Configuration configuration) {
      var token = deployment.getConfig().getContainers().get(0).getEnvironment().get("MQ_JUPYTER_TOKEN");
      var parameters = DeployedStackParameters
         .apply(deployment.getProperties().get(0).getMappedPortUrls().get(8888).toString().replace("localhost", "hub.maquette.ai.internal") + "?token=" + token, "Launch Jupyter Notebook")
         .withParameter("Python Version", configuration.getVersion());
      return CompletableFuture.completedFuture(parameters);
   }

   @Value
   @AllArgsConstructor(staticName = "apply")
   @NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
   public static class Configuration implements StackConfiguration {

      String version;

      @Override
      @JsonIgnore
      public String getStackName() {
         return STACK_NAME;
      }
   }

}
