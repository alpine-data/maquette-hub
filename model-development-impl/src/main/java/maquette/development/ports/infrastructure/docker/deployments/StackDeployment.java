package maquette.development.ports.infrastructure.docker.deployments;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import maquette.development.ports.infrastructure.docker.Deployment;
import maquette.development.ports.infrastructure.docker.model.DeploymentConfig;
import maquette.development.values.stacks.StackConfiguration;
import maquette.development.values.stacks.StackInstanceParameters;

import java.util.concurrent.CompletionStage;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type")
@JsonSubTypes(
    {
        @JsonSubTypes.Type(value = MlflowStackDeployment.class, name = "mlflow"),
        @JsonSubTypes.Type(value = PythonStackDeployment.class, name = "python"),
        @JsonSubTypes.Type(value = MLWorkspaceDeployment.class, name = "ml-workspace")
    })
public interface StackDeployment {

    String getStackInstanceName();

    DeploymentConfig getDeploymentConfig();

    StackConfiguration getStackConfiguration();

    CompletionStage<StackInstanceParameters> getInstanceParameters(Deployment deployment);

}
