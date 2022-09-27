package maquette.development.values.stacks;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
public abstract class DefaultStackConfiguration implements StackConfiguration {

    protected static final String NAME = "name";
    protected static final String ENVIRONMENT = "environment";

    /**
     * The name of the Python instance.
     */
    @JsonProperty(NAME)
    String name;


    /**
     * Environment variables which should be set in the stacks nodes.
     */
    @JsonProperty(ENVIRONMENT)
    Map<String, String> environmentVariables;

    @Override
    public String getStackInstanceName() {
        return name;
    }

    @Override
    public StackConfiguration withStackInstanceName(String name) {
        this.name = name;
        return this;
    }

    public Map<String, String> getEnvironmentVariables() {
        return Map.copyOf(environmentVariables);
    }

    @Override
    public StackConfiguration withEnvironmentVariables(Map<String, String> environment) {
        this.environmentVariables = environment;
        return this;
    }

}
