package maquette.development.values.stacks;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.development.values.EnvironmentType;

import java.util.List;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class SynapseStackConfiguration implements StackConfiguration {

    private static final String NAME = "name";

    private static final String SPARK_SIZE = "sparkSize";

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

    @JsonCreator
    public static SynapseStackConfiguration apply(
        @JsonProperty(NAME) String name,
        @JsonProperty(SPARK_SIZE) String sparkSize) {

        return new SynapseStackConfiguration(name, sparkSize);
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
    public StackInstanceParameters getInstanceParameters(Map<String, String> parameters, EnvironmentType environment) {
        return StackInstanceParameters.apply("http://foo.bar", "Open Notebook");
    }

    @Override
    public StackConfiguration withStackInstanceName(String name) {
        return SynapseStackConfiguration.apply(name, sparkSize);
    }

}
