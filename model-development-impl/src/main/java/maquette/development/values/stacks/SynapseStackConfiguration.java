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
public final class SynapseStackConfiguration extends DefaultStackConfiguration {

    private static final String SPARK_SIZE = "sparkSize";

    /**
     * A T-Shirt size indicating resources of the Workspace.
     */
    @JsonProperty(SPARK_SIZE)
    private final String sparkSize;

    @JsonCreator
    public static SynapseStackConfiguration apply(
        @JsonProperty(NAME) String name,
        @JsonProperty(SPARK_SIZE) String sparkSize,
        @JsonProperty(ENVIRONMENT) Map<String, String> environmentVariables) {

        if (Objects.isNull(environmentVariables)) {
            environmentVariables = Maps.newHashMap();
        }

        var instance = new SynapseStackConfiguration(sparkSize);
        instance.name = name;
        instance.environmentVariables = environmentVariables;
        return instance;
    }

    @Override
    public List<String> getResourceGroups() {
        return Lists.newArrayList();
    }

}
