package maquette.development.values.stacks;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PythonStackConfiguration extends DefaultStackConfiguration {

    private static final String RESOURCE_GROUPS = "resourceGroups";
    private static final String VERSION = "version";
    private static final String MEMORY_REQUEST = "memoryRequest";


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
     * Memory to requested and used for the container in the following format
     * https://kubernetes.io/docs/reference/kubernetes-api/common-definitions/quantity/
     */
    @JsonProperty(MEMORY_REQUEST)
    String memoryRequest;


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

        if (Objects.isNull(resourceGroups)) {
            resourceGroups = Lists.newArrayList();
        }

        if (Objects.isNull(memoryRequest)) {
            memoryRequest = "4Gi";
        }

        var instance = new PythonStackConfiguration(resourceGroups, version, memoryRequest);
        instance.name = name;
        instance.environmentVariables = environmentVariables;
        return instance;
    }

    @Override
    public List<String> getResourceGroups() {
        return resourceGroups;
    }

}
