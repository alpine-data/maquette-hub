package maquette.development.values.stacks;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import maquette.core.common.Operators;

import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor(staticName = "apply")
public final class MlflowStackConfiguration implements StackConfiguration {

    private static final String NAME = "name";
    private static final String SAS_EXPIRY_TOKEN = "sasExpiryToken";
    private static final String RESOURCE_GROUPS = "resourceGroups";

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

    @Override
    public String getStackInstanceName() {
        return name;
    }

    @Override
    public List<String> getResourceGroups() {
        return resourceGroups;
    }

    @Override
    public StackInstanceParameters getInstanceParameters(Map<String, String> parameters) {
        // Add MLFLOW specific environment variables to parameters (MLFFLOW_ENDPOINT, MLFLOW_SE_...)

        return StackInstanceParameters.apply(
            Operators.suppressExceptions(() -> new URL(parameters.get("foo").toString())), "MLFlow Dashboard", parameters);
    }

}
