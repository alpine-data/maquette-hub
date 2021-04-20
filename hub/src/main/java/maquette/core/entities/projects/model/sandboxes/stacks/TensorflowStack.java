package maquette.core.entities.projects.model.sandboxes.stacks;

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
import maquette.core.entities.infrastructure.model.DataVolume;
import maquette.core.entities.infrastructure.model.DeploymentConfig;
import maquette.core.entities.infrastructure.model.DeploymentProperties;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.entities.projects.model.sandboxes.SandboxProperties;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
public class TensorflowStack implements Stack<TensorflowStack.Configuration> {

   public static final String STACK_NAME = "tensorflow";

   @Override
   public String getTitle() {
      return "Python with Tensorflow";
   }

   @Override
   public String getName() {
      return STACK_NAME;
   }

   @Override
   public String getSummary() {
      return "A default python stack with Python Kernel, Miniconda, Jupyter and pre-configured Tensorflow.";
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
   public DeploymentConfig getDeploymentConfig(ProjectProperties project, SandboxProperties sandbox, DataVolume volume, Configuration properties, Map<String, String> projectEnvironment) {
      var postgresContainerCfg = ContainerConfig
         .builder(String.format("mq__%s_%s__jupyter", project.getId(), sandbox.getId()), "mq-stacks--python:latest")
         .withEnvironmentVariable("MQ_USERNAME", sandbox.getCreated().getBy())
         .withEnvironmentVariable("MQ_JUPYTER_TOKEN", Operators.randomHash())
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
