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
import maquette.development.values.stacks.PythonStackConfiguration;
import maquette.development.values.stacks.StackConfiguration;
import maquette.development.values.stacks.StackInstanceParameters;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PythonStackDeployment implements StackDeployment {

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
    public static PythonStackDeployment apply(
        @JsonProperty(DEPLOYMENT) DeploymentConfig deploymentConfig,
        @JsonProperty(STACK) StackConfiguration stackConfiguration,
        @JsonProperty(JUPYTER_TOKEN) String jupyterToken,
        @JsonProperty(STACK_HASH) String stackHash) {

        return new PythonStackDeployment(deploymentConfig, stackConfiguration, jupyterToken, stackHash);
    }

    public static PythonStackDeployment apply(PythonStackConfiguration configuration) {
        var jupyterToken = Operators.randomHash();
        var stackHash = Operators.randomHash();

        var pythonContainer = ContainerConfig
            .builder(configuration.getStackInstanceName(), "spacereg.azurecr.io/space-python-jupyter38:latest")
            .withEnvironmentVariable("MQ_JUPYTER_TOKEN", jupyterToken)
            .withEnvironmentVariable(StackConfiguration.PARAM_STACK_TOKEN, stackHash)
            .withEnvironmentVariables(configuration.getEnvironmentVariables())
            .withHostName("python")
            .withPort(8888)
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
                    .get(8888)
                    .toString(), this.jupyterToken);
                return StackInstanceParameters.encodeAndCreate(url, "Launch Jupyter Notebook", params);
            });
    }


}
