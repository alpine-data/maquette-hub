package maquette.development.values.stacks;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import maquette.core.values.UID;
import org.checkerframework.checker.nullness.Opt;

import javax.swing.text.html.Option;
import java.util.Map;
import java.util.Optional;

@NoArgsConstructor
public abstract class DefaultStackConfiguration implements StackConfiguration {

    protected static final String NAME = "name";
    protected static final String ENVIRONMENT = "environment";
    protected static final String VOLUME = "volume";

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

    /**
     * A volume that will be mounted to the stack
     */
    @JsonProperty(VOLUME)
    UID volume;

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

    @Override
    public Optional<UID> getVolume() {
        return Optional.ofNullable(volume);
    }

    @Override
    public StackConfiguration withVolume(UID volume) {
        this.volume = volume;
        return this;
    }

}
