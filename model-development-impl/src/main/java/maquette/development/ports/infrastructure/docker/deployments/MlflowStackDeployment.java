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
import maquette.development.values.stacks.MlflowStackConfiguration;
import maquette.development.values.stacks.StackConfiguration;
import maquette.development.values.stacks.StackInstanceParameters;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MlflowStackDeployment implements StackDeployment {

    private static final String MINIO_REGION = "mzg";

    private static final String DEPLOYMENT = "deployment";
    private static final String STACK = "stack";

    private static final String MINIO_CONTAINER_NAME = "minio-container-name";
    private static final String MLFLOW_CONTAINER_NAME = "mlflow-container-name";
    private static final String MINIO_ACCESS_KEY = "minio-access-key";
    private static final String MINIO_ACCESS_SECRET = "minio-access-secret";

    @JsonProperty(DEPLOYMENT)
    DeploymentConfig deploymentConfig;

    @JsonProperty(STACK)
    StackConfiguration stackConfiguration;

    @JsonProperty(MLFLOW_CONTAINER_NAME)
    String mlflowContainerName;

    @JsonProperty(MINIO_CONTAINER_NAME)
    String minioContainerName;

    @JsonProperty(MINIO_ACCESS_KEY)
    String minioAccessKey;

    @JsonProperty(MINIO_ACCESS_SECRET)
    String minioAccessSecret;

    @JsonCreator
    public static MlflowStackDeployment apply(
        @JsonProperty(DEPLOYMENT) DeploymentConfig deploymentConfig,
        @JsonProperty(STACK) StackConfiguration stackConfiguration,
        @JsonProperty(MLFLOW_CONTAINER_NAME) String mlflowContainerName,
        @JsonProperty(MINIO_CONTAINER_NAME) String minioContainerName,
        @JsonProperty(MINIO_ACCESS_KEY) String minioAccessKey,
        @JsonProperty(MINIO_ACCESS_SECRET) String minioAccessSecret) {

        return new MlflowStackDeployment(
            deploymentConfig, stackConfiguration, mlflowContainerName,
            minioContainerName, minioAccessKey, minioAccessSecret);
    }

    public static MlflowStackDeployment apply(MlflowStackConfiguration configuration) {
        var mlflowContainerName = String.format("%s--mlflow", configuration.getStackInstanceName());

        var minioContainerName = String.format("%s--minio", configuration.getStackInstanceName());
        var minioAccessKey = Operators.randomHash();
        var minioAccessSecret = Operators.randomHash();

        var minioContainerCfg = ContainerConfig
            .builder(minioContainerName, "mq--mlflow-minio:latest") // Check Container...
            .withEnvironmentVariable("MINIO_ACCESS_KEY", minioAccessKey)
            .withEnvironmentVariable("MINIO_SECRET_KEY", minioAccessSecret)
            .withEnvironmentVariable("MINIO_REGION_NAME", "mzg")
            .withHostName("minio")
            .withNetwork(configuration.getStackInstanceName())
            .withPort(9000)
            .withPort(9001)
            .build();

        var postgresContainerCfg = ContainerConfig
            .builder(String.format("%s--psql", configuration.getStackInstanceName()), "postgres:14.1")
            .withEnvironmentVariable("POSTGRES_USER", "maquette")
            .withEnvironmentVariable("POSTGRES_PASSWORD", Operators.randomHash())
            .withEnvironmentVariable("PGDATA", "/data")
            .withHostName("postgres")
            .withPort(5432)
            .build();

        var mlflowContainerCfg = ContainerConfig
            .builder(mlflowContainerName, "mq--mlflow:latest")
            .withEnvironmentVariable("MLFLOW_S3_ENDPOINT_URL", "http://minio:9000")
            .withEnvironmentVariable("AWS_ACCESS_KEY_ID", minioContainerCfg
                .getEnvironment()
                .get("MINIO_ACCESS_KEY"))
            .withEnvironmentVariable("AWS_SECRET_ACCESS_KEY", minioContainerCfg
                .getEnvironment()
                .get("MINIO_SECRET_KEY"))
            .withEnvironmentVariable("AWS_DEFAULT_REGION", MINIO_REGION)
            .withEnvironmentVariable("POSTGRES_USERNAME", postgresContainerCfg
                .getEnvironment()
                .get("POSTGRES_USER"))
            .withEnvironmentVariable("POSTGRES_PASSWORD", postgresContainerCfg
                .getEnvironment()
                .get("POSTGRES_PASSWORD"))
            .withEnvironmentVariable("POSTGRES_HOST", "postgres")
            .withHostName("mlflow")
            .withNetwork(configuration.getStackInstanceName())
            .withPort(5000)
            .build();

        var deploymentConfig = DeploymentConfig
            .builder(configuration.getStackInstanceName())
            .withContainerConfig(minioContainerCfg)
            .withContainerConfig(postgresContainerCfg)
            .withContainerConfig(mlflowContainerCfg)
            .build();

        return apply(deploymentConfig, configuration, mlflowContainerName, minioContainerName, minioAccessKey,
            minioAccessSecret);
    }

    @Override
    public String getStackInstanceName() {
        return stackConfiguration.getStackInstanceName();
    }

    @Override
    public CompletionStage<StackInstanceParameters> getInstanceParameters(Deployment deployment) {
        var mlflowPortsCS = deployment
            .getContainer(mlflowContainerName)
            .getMappedPortUrls();
        var minioPortsCS = deployment
            .getContainer(minioContainerName)
            .getMappedPortUrls();

        return Operators.compose(mlflowPortsCS, minioPortsCS, (mlflowPorts, minioPorts) -> {
            var mlflowUrl = mlflowPorts
                .get(5000)
                .toString()
                .replace("localhost", "host.docker.internal");
            var minioUrl = minioPorts
                .get(9000)
                .toString()
                .replace("localhost", "host.docker.internal");

            var parameters = Maps.<String, String>newHashMap();

            parameters.put(MlflowStackConfiguration.PARAM_MLFFLOW_ENDPOINT, mlflowUrl);
            parameters.put(MlflowStackConfiguration.PARAM_INTERNAL_MLFLOW_ENDPOINT, mlflowUrl);

            parameters.put(MlflowStackConfiguration.PARAM_MLFLOW_TRACKING_URL, mlflowUrl);
            parameters.put(MlflowStackConfiguration.PARAM_INTERNAL_MLFLOW_TRACKING_URL, mlflowUrl);

            parameters.put("MLFLOW_S3_ENDPOINT_URL", minioUrl);
            parameters.put("AWS_ACCESS_KEY_ID", minioAccessKey);
            parameters.put("AWS_SECRET_ACCESS_KEY", minioAccessSecret);
            parameters.put("AWS_DEFAULT_REGION", MINIO_REGION);

            return StackInstanceParameters.encodeAndCreate(mlflowUrl, "Launch MLflow Dashboard", parameters);
        });
    }


}
