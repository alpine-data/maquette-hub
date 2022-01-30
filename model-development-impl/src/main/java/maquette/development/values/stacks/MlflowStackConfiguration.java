package maquette.development.values.stacks;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.common.Operators;
import maquette.core.values.UID;
import maquette.development.entities.mlflow.MlflowConfiguration;
import maquette.development.values.EnvironmentType;

import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@With
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MlflowStackConfiguration implements StackConfiguration {

    private static final String NAME = "name";
    private static final String SAS_EXPIRY_TOKEN = "sasExpiryToken";
    private static final String RESOURCE_GROUPS = "resourceGroups";

    /**
     * Defines the public URL to access the MLflow instance.
     *
     * Parameter must be set by infrastructure implementation and returned with
     * {@link maquette.development.ports.infrastructure.InfrastructurePort#getInstanceParameters(UID, String, EnvironmentType)}.
     */
    public static final String PARAM_MLFFLOW_ENDPOINT = "MLFFLOW_ENDPOINT";

    /**
     * Defines the public tracking URL which is used for experiment tracking from external environments (e.g. local).
     *
     * Parameter must be set by infrastructure implementation and returned with
     * {@link maquette.development.ports.infrastructure.InfrastructurePort#getInstanceParameters(UID, String, EnvironmentType)}.
     */
    public static final String PARAM_MLFLOW_TRACKING_URL = "MLFLOW_TRACKING_URL";

    /**
     * Defines the URL which is required by Maquette Hub and Sandboxes to access the MLFlow API.
     *
     * Parameter must be set by infrastructure implementation and returned with
     * {@link maquette.development.ports.infrastructure.InfrastructurePort#getInstanceParameters(UID, String, EnvironmentType)}.
     */
    public static final String PARAM_INTERNAL_MLFLOW_ENDPOINT = "INTERNAL_MLFLOW_ENDPOINT";

    /**
     * Defines the tracking URL which is used for experiment tracking from internal environments (e.g. hub, sandboxes).
     *
     * Parameter must be set by infrastructure implementation and returned with
     * {@link maquette.development.ports.infrastructure.InfrastructurePort#getInstanceParameters(UID, String, EnvironmentType)}.
     */
    public static final String PARAM_INTERNAL_MLFLOW_TRACKING_URL = "MLFLOW_TRACKING_URL";

    /**
     * The name of the MLflow instance.
     */
    @JsonProperty(NAME)
    String name;

    /**
     * The SASToken used for user authentication against MLFlows object storage.
     */
    @JsonProperty(SAS_EXPIRY_TOKEN)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'+00:00'", timezone = "UTC")
    Instant sasTokenExpiry;

    /**
     * A list of MARS managed resource groups which are related to this MLflow instance.
     * Might be used for tagging of resources, or creation of private networks between the groups.
     */
    @JsonProperty(RESOURCE_GROUPS)
    List<String> resourceGroups;

    @JsonCreator
    public static MlflowStackConfiguration apply(@JsonProperty(NAME) String name,
                                                  @JsonProperty(SAS_EXPIRY_TOKEN) Instant sasTokenExpiry,
                                                  @JsonProperty(RESOURCE_GROUPS) List<String> resourceGroups) {
        return new MlflowStackConfiguration(name, sasTokenExpiry, resourceGroups);
    }

    @Override
    public String getStackInstanceName() {
        return name;
    }

    @Override
    public List<String> getResourceGroups() {
        return resourceGroups;
    }

    @Override
    public StackInstanceParameters getInstanceParameters(Map<String, String> parameters, EnvironmentType environment) {
        URL mlflowEndpoint = null;
        var mlflowEndpointLabel = "MLflow Dashboard";

        if (parameters.containsKey(PARAM_MLFFLOW_ENDPOINT)) {
            mlflowEndpoint = Operators.suppressExceptions(() -> new URL((parameters.get(PARAM_MLFFLOW_ENDPOINT))));
        }

        return StackInstanceParameters.apply(mlflowEndpoint, mlflowEndpointLabel, parameters);
    }

    public Optional<MlflowConfiguration> getMlflowConfiguration(StackInstanceParameters parameters) {
        if (parameters.getParameters().containsKey(PARAM_INTERNAL_MLFLOW_TRACKING_URL)) {
            return Optional.of(MlflowConfiguration.apply(
                parameters.getParameters().get(PARAM_INTERNAL_MLFLOW_TRACKING_URL).toString(),
                ""));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public StackConfiguration withStackInstanceName(String name) {
        return MlflowStackConfiguration.apply(name, sasTokenExpiry, resourceGroups);
    }

}
