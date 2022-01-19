package maquette.development.values.stacks;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.common.Operators;

import java.net.URL;
import java.util.List;
import java.util.Map;

@With
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class PythonStackConfiguration implements StackConfiguration {

    private static final String NAME = "name";
    private static final String RESOURCE_GROUPS = "resourceGroups";
    private static final String VERSION = "version";
    private static final String MEMORY_REQUEST = "memoryRequest";

    /**
     * The name of the Python instance.
     */
    @JsonProperty(NAME)
    String name;

    /**
     * A list of MARS managed resource groups which are related to this instance.
     * Might be used for tagging of resources, or creation of private networks between the groups.
     */
    @JsonProperty(RESOURCE_GROUPS)
    List<String> resourceGroups;

    /**
     * The python version.
     */
    @JsonProperty(VERSION)
    String version;

    /**
     * Memory to requested and used for the container in the following format https://kubernetes.io/docs/reference/kubernetes-api/common-definitions/quantity/
     */
    @JsonProperty(MEMORY_REQUEST)
    String memoryRequest;

    @JsonCreator
    public static PythonStackConfiguration apply(@JsonProperty(NAME) String name,
                                                 @JsonProperty(RESOURCE_GROUPS) List<String> resourceGroups,
                                                 @JsonProperty(MEMORY_REQUEST) String memoryRequest,
                                                 @JsonProperty(VERSION) String version) {
        return new PythonStackConfiguration(name, resourceGroups, version, memoryRequest);
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
    public StackInstanceParameters getInstanceParameters(Map<String, String> parameters) {

        return StackInstanceParameters.apply(
            Operators.suppressExceptions(() -> new URL(String.format("%s/?token=%s", parameters.get(
                "ENTRY_POINT_ENDPOINT"), parameters.get("MQ_JUPYTER_TOKEN")))), "Python sandbox", parameters);
    }

    @Override
    public StackConfiguration withStackInstanceName(String name) {
        return PythonStackConfiguration.apply(name, resourceGroups, memoryRequest, version);
    }

}
