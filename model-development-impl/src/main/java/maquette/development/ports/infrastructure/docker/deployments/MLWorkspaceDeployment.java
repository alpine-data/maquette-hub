package maquette.development.ports.infrastructure.docker.deployments;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.common.Operators;
import maquette.development.ports.infrastructure.docker.Deployment;
import maquette.development.ports.infrastructure.docker.model.ContainerConfig;
import maquette.development.ports.infrastructure.docker.model.DeploymentConfig;
import maquette.development.values.stacks.MLWorkspaceStackConfiguration;
import maquette.development.values.stacks.PythonStackConfiguration;
import maquette.development.values.stacks.StackConfiguration;
import maquette.development.values.stacks.StackInstanceParameters;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MLWorkspaceDeployment implements StackDeployment {

    private static final String MINIO_REGION = "mzg";

    private static final String DEPLOYMENT = "deployment";
    private static final String STACK = "stack";

    private static final String JUPYTER_TOKEN = "jupyter-token";
    private static final String STACK_HASH = "stack-hash";

    @JsonProperty(DEPLOYMENT)
    DeploymentConfig deploymentConfig;

    @JsonProperty(STACK)
    StackConfiguration stackConfiguration;

    @JsonProperty(JUPYTER_TOKEN)
    String jupyterToken;

    @JsonProperty(STACK_HASH)
    String stackHash;

    @JsonCreator
    public static MLWorkspaceDeployment apply(
        @JsonProperty(DEPLOYMENT) DeploymentConfig deploymentConfig,
        @JsonProperty(STACK) StackConfiguration stackConfiguration,
        @JsonProperty(JUPYTER_TOKEN) String jupyterToken,
        @JsonProperty(STACK_HASH) String stackHash) {

        return new MLWorkspaceDeployment(deploymentConfig, stackConfiguration, jupyterToken, stackHash);
    }

    public static MLWorkspaceDeployment apply(MLWorkspaceStackConfiguration configuration) {
        var jupyterToken = Operators.randomHash();
        var stackHash = Operators.randomHash();

        var pythonContainer = ContainerConfig
            .builder(configuration.getStackInstanceName(), "mltooling/ml-workspace:0.13.2")
            .withEnvironmentVariable("AUTHENTICATE_VIA_JUPYTER", jupyterToken)
            .withEnvironmentVariable(StackConfiguration.PARAM_STACK_TOKEN, stackHash)
            .withEnvironmentVariables(configuration.getEnvironmentVariables())
            .withHostName("ml-workspace")
            .withPort(8080)
            .build();

        var deploymentConfig = DeploymentConfig
            .builder(configuration.getStackInstanceName())
            .withContainerConfig(pythonContainer)
            .build();

        return apply(deploymentConfig, configuration, jupyterToken, stackHash);
    }

    @Override
    public String getStackInstanceName() {
        return stackConfiguration.getStackInstanceName();
    }

    @Override
    public CompletionStage<StackInstanceParameters> getInstanceParameters(Deployment deployment) {
        return deployment
            .getContainer(this.getStackInstanceName())
            .getMappedPortUrls()
            .thenApply(ports -> {
                var params = Maps.<String, String>newHashMap();
                params.put(StackConfiguration.PARAM_STACK_TOKEN, stackHash);

                var url = String.format("%s?token=%s", ports
                    .get(8080)
                    .toString(), this.jupyterToken);
                return StackInstanceParameters.encodeAndCreate(url, "Launch ML Workspace IDE", params);
            });
    }


}
