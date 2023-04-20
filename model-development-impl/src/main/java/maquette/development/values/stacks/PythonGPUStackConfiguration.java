package maquette.development.values.stacks;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@With
@Value
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PythonGPUStackConfiguration extends DefaultStackConfiguration {

    private static final String RESOURCE_GROUPS = "resourceGroups";
    private static final String VERSION = "version";
    private static final String SIZE = "size";

    private static final String USER_EMAIL = "userEmail";


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
     * <a href="https://kubernetes.io/docs/reference/kubernetes-api/common-definitions/quantity/">k8s docs</a>
     */
    @JsonProperty(SIZE)
    String size;

    @JsonCreator
    public static PythonGPUStackConfiguration apply(
        @JsonProperty(NAME) String name,
        @JsonProperty(RESOURCE_GROUPS) List<String> resourceGroups,
        @JsonProperty(SIZE) String size,
        @JsonProperty(VERSION) String version,
        @JsonProperty(ENVIRONMENT) Map<String, String> environmentVariables,
        @JsonProperty(USER_EMAIL) String userEmail) {

        if (Objects.isNull(environmentVariables)) {
            environmentVariables = Maps.newHashMap();
        }

        if (Objects.isNull(resourceGroups)) {
            resourceGroups = Lists.newArrayList();
        }

        if (Objects.isNull(size)) {
            size = "gpusmall";
        }

        var instance = new PythonGPUStackConfiguration(resourceGroups, version, size);
        instance.name = name;
        instance.environmentVariables = environmentVariables;
        instance.userEmail = userEmail;

        return instance;
    }

    @Override
    public List<String> getResourceGroups() {
        return resourceGroups;
    }

}
