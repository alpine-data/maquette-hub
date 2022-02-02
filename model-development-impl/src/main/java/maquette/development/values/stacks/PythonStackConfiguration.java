package maquette.development.values.stacks;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@With
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PythonStackConfiguration implements StackConfiguration {

    private static final String NAME = "name";
    private static final String RESOURCE_GROUPS = "resourceGroups";
    private static final String VERSION = "version";
    private static final String MEMORY_REQUEST = "memoryRequest";
    private static final String ENVIRONMENT = "environment";

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
     * Memory to requested and used for the container in the following format https://kubernetes
     * .io/docs/reference/kubernetes-api/common-definitions/quantity/
     */
    @JsonProperty(MEMORY_REQUEST)
    String memoryRequest;

    /**
     * Environment variables which should be set in the stacks nodes.
     */
    @JsonProperty(ENVIRONMENT)
    Map<String, String> environmentVariables;

    @JsonCreator
    public static PythonStackConfiguration apply(
        @JsonProperty(NAME) String name,
        @JsonProperty(RESOURCE_GROUPS) List<String> resourceGroups,
        @JsonProperty(MEMORY_REQUEST) String memoryRequest,
        @JsonProperty(VERSION) String version,
        @JsonProperty(ENVIRONMENT) Map<String, String> environmentVariables) {

        if (Objects.isNull(environmentVariables)) {
            environmentVariables = Maps.newHashMap();
        }

        return new PythonStackConfiguration(name, resourceGroups, version, memoryRequest, environmentVariables);
    }

    @Override
    public String getStackInstanceName() {
        return name;
    }

    @Override
    public List<String> getResourceGroups() {
        return resourceGroups;
    }

    public Map<String, String> getEnvironmentVariables() {
        return Map.copyOf(environmentVariables);
    }

    @Override
    public StackConfiguration withStackInstanceName(String name) {
        return PythonStackConfiguration.apply(name, resourceGroups, memoryRequest, version, environmentVariables);
    }

}
