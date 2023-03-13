package maquette.development.values.stacks;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import maquette.core.values.UID;
import maquette.development.entities.mlflow.MlflowConfiguration;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MlflowStackConfiguration extends DefaultStackConfiguration {

    private static final String SAS_EXPIRY_TOKEN = "sasExpiryToken";
    private static final String RESOURCE_GROUPS = "resourceGroups";

    /**
     * Defines the public URL to access the MLflow instance.
     * <p>
     * Parameter must be set by infrastructure implementation and returned with
     * {@link maquette.development.ports.infrastructure.InfrastructurePort#getInstanceParameters(UID, String)}.
     */
    public static final String PARAM_MLFFLOW_ENDPOINT = "MLFFLOW_ENDPOINT";

    /**
     * Defines the public tracking URL which is used for experiment tracking from external environments (e.g. local).
     * <p>
     * Parameter must be set by infrastructure implementation and returned with
     * {@link maquette.development.ports.infrastructure.InfrastructurePort#getInstanceParameters(UID, String)}.
     */
    public static final String PARAM_MLFLOW_TRACKING_URL = "MLFLOW_TRACKING_URI";

    /**
     * Defines the S3 storage account URL where MLflow clients save their artifacts.
     */
    public static final String PARAM_MLFLOW_S3_ENDPOINT_URL = "MLFLOW_S3_ENDPOINT_URL";

    /**
     * Defines the URL which is required by Maquette Hub and Sandboxes to access the MLFlow API.
     * <p>
     * Parameter must be set by infrastructure implementation and returned with
     * {@link maquette.development.ports.infrastructure.InfrastructurePort#getInstanceParameters(UID, String)}.
     */
    public static final String PARAM_INTERNAL_MLFLOW_ENDPOINT = "INTERNAL_MLFLOW_ENDPOINT";

    /**
     * Defines the tracking URL which is used for experiment tracking from internal environments (e.g. hub, sandboxes).
     * <p>
     * Parameter must be set by infrastructure implementation and returned with
     * {@link maquette.development.ports.infrastructure.InfrastructurePort#getInstanceParameters(UID, String)}.
     */
    public static final String PARAM_INTERNAL_MLFLOW_TRACKING_URL = "INTERNAL_MLFLOW_TRACKING_URI";

    /**
     * Defines the S3 storage account URL where MLflow clients within internal network (sandboxes) to
     * save their artifacts.
     */
    public static final String PARAM_INTERNAL_MLFLOW_S3_ENDPOINT_URL = "INTERNAL_MLFLOW_S3_ENDPOINT_URL";

    /**
     * The SASToken used for user authentication against MLFlows object storage.
     */
    @JsonProperty(SAS_EXPIRY_TOKEN)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'+00:00'", timezone = "UTC")
    @Getter
    Instant sasTokenExpiry;

    /**
     * A list of MARS managed resource groups which are related to this MLflow instance.
     * Might be used for tagging of resources, or creation of private networks between the groups.
     */
    @JsonProperty(RESOURCE_GROUPS)
    List<String> resourceGroups;

    @JsonCreator
    public static MlflowStackConfiguration apply(
        @JsonProperty(NAME) String name,
        @JsonProperty(SAS_EXPIRY_TOKEN) Instant sasTokenExpiry,
        @JsonProperty(RESOURCE_GROUPS) List<String> resourceGroups,
        @JsonProperty(ENVIRONMENT) Map<String, String> environmentVariables) {

        if (Objects.isNull(environmentVariables)) {
            environmentVariables = Maps.newHashMap();
        }

        var instance = new MlflowStackConfiguration(sasTokenExpiry, resourceGroups);
        instance.name = name;
        instance.environmentVariables = environmentVariables;
        return instance;
    }

    @Override
    public List<String> getResourceGroups() {
        return resourceGroups;
    }

    /**
     * Add an expiry timestamp for the sas token to the stack.
     *
     * @param sasTokenExpiry The expiry timestamp for the sas token
     * @return A new instance of this configuration with updated expiry timestamp for the sas token
     */
    public MlflowStackConfiguration withSasTokenExpiry(Instant sasTokenExpiry) {
        this.sasTokenExpiry = sasTokenExpiry;
        return this;
    }

    public Optional<MlflowConfiguration> getMlflowConfiguration(StackInstanceParameters parameters) {
        if (parameters
            .getParameters()
            .containsKey(PARAM_INTERNAL_MLFLOW_TRACKING_URL)) {
            var trackingUrl = parameters
                .getParametersDecoded()
                .get(PARAM_INTERNAL_MLFLOW_TRACKING_URL);
            return Optional.of(MlflowConfiguration.apply(trackingUrl, ""));
        } else {
            return Optional.empty();
        }
    }
}
