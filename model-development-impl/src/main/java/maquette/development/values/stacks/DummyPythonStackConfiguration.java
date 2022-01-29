package maquette.development.values.stacks;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DummyPythonStackConfiguration implements StackConfiguration {

    private static final String NAME = "name";

    private static final String VERSION = "version";

    /**
     * The name of the stack instance.
     */
    @JsonProperty(NAME)
    private final String name;

    /**
     * The Python Version which should be configured in the environment.
     */
    @JsonProperty(VERSION)
    private final String version;

    /**
     * Creates a new instance.
     *
     * @param name The name of the stack instance.
     * @param version The Python Version which should be configured in the environment.
     * @return A new instance.
     */
    @JsonCreator
    public static DummyPythonStackConfiguration apply(
        @JsonProperty(NAME) String name,
        @JsonProperty(VERSION) String version) {

        return new DummyPythonStackConfiguration(name, version);
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
    public StackInstanceParameters getInstanceParameters(Map<String, String> parameters) {
        return StackInstanceParameters.apply("http://foo.bar", "Open Notebook");
    }

    @Override
    public StackConfiguration withStackInstanceName(String name) {
        return DummyPythonStackConfiguration.apply(name, version);
    }

}
