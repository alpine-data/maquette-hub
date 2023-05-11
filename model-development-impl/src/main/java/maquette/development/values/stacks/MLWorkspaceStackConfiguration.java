package maquette.development.values.stacks;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Value
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MLWorkspaceStackConfiguration extends DefaultStackConfiguration {

    private static final String RESOURCE_GROUPS = "resourceGroups";
    private static final String MEMORY_REQUEST = "memoryRequest";


    /**
     * A list of MARS managed resource groups which are related to this instance.
     * Might be used for tagging of resources, or creation of private networks between the groups.
     */
    @JsonProperty(RESOURCE_GROUPS)
    List<String> resourceGroups;

    /**
     * Memory to requested and used for the container in the following format
     * <a href="https://kubernetes.io/docs/reference/kubernetes-api/common-definitions/quantity/">k8s docs</a>
     */
    @JsonProperty(MEMORY_REQUEST)
    String memoryRequest;

    @JsonCreator
    public static MLWorkspaceStackConfiguration apply(
        @JsonProperty(NAME) String name,
        @JsonProperty(RESOURCE_GROUPS) List<String> resourceGroups,
        @JsonProperty(MEMORY_REQUEST) String memoryRequest,
        @JsonProperty(ENVIRONMENT) Map<String, String> environmentVariables,
        @JsonProperty(USER_EMAIL) String userEmail,
        @JsonProperty(SECURED) Boolean secured) {

        if (Objects.isNull(environmentVariables)) {
            environmentVariables = Maps.newHashMap();
        }

        if (Objects.isNull(resourceGroups)) {
            resourceGroups = Lists.newArrayList();
        }

        if (Objects.isNull(memoryRequest)) {
            memoryRequest = "4Gi";
        }

        var instance = new MLWorkspaceStackConfiguration(resourceGroups, memoryRequest);
        instance.name = name;
        instance.environmentVariables = environmentVariables;
        instance.userEmail = userEmail;
        instance.secured = secured == null || Boolean.TRUE.equals(secured);
        
        return instance;
    }

    @Override
    public List<String> getResourceGroups() {
        return resourceGroups;
    }

}
