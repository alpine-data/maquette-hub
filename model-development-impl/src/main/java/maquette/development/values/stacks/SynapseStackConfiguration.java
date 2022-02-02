package maquette.development.values.stacks;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class SynapseStackConfiguration implements StackConfiguration {

    private static final String NAME = "name";

    private static final String SPARK_SIZE = "sparkSize";

    private static final String ENVIRONMENT = "environment";

    /**
     * The name of the stack instance.
     */
    @JsonProperty(NAME)
    private final String name;

    /**
     * A T-Shirt size indicating resources of the Workspace.
     */
    @JsonProperty(SPARK_SIZE)
    private final String sparkSize;

    /**
     * Environment variables which should be set in the stacks nodes.
     */
    @JsonProperty(ENVIRONMENT)
    Map<String, String> environmentVariables;

    @JsonCreator
    public static SynapseStackConfiguration apply(
        @JsonProperty(NAME) String name,
        @JsonProperty(SPARK_SIZE) String sparkSize,
        @JsonProperty(ENVIRONMENT) Map<String, String> environmentVariables) {

        if (Objects.isNull(environmentVariables)) {
            environmentVariables = Maps.newHashMap();
        }

        return new SynapseStackConfiguration(name, sparkSize, environmentVariables);
    }

    @Override
    public String getStackInstanceName() {
        return name;
    }

    @Override
    public List<String> getResourceGroups() {
        return Lists.newArrayList();
    }

    @Override
    public Map<String, String> getEnvironmentVariables() {
        return Map.copyOf(environmentVariables);
    }

    @Override
    public StackConfiguration withStackInstanceName(String name) {
        return SynapseStackConfiguration.apply(name, sparkSize, environmentVariables);
    }

    @Override
    public StackConfiguration withEnvironmentVariables(Map<String, String> environment) {
        return SynapseStackConfiguration.apply(name, sparkSize, environment);
    }

}
