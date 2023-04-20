package maquette.development.values.stacks;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import maquette.core.values.UID;

import java.util.Map;
import java.util.Optional;

/**
 * The default stack configuration is used to implement settings which are set by the Maquette Backend
 * as well as common settings.
 *
 * All settings which are specified in child classes must be configurable by users.
 */
@NoArgsConstructor
public abstract class DefaultStackConfiguration implements StackConfiguration {

    protected static final String NAME = "name";
    protected static final String ENVIRONMENT = "environment";
    protected static final String VOLUME = "volume";
    protected static final String COST = "cost";
    protected static final String USER_EMAIL = "userEmail";

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

    /**
     * The cost of the stack.
     */
    @JsonProperty(COST)
    double cost;

    @JsonProperty(USER_EMAIL)
    String userEmail;

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
    public Optional<String> getUserEmail() {
        return Optional.ofNullable(this.userEmail);
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

    @Override
    public StackConfiguration withCost(double cost) {
        this.cost = cost;
        return this;
    }

    @Override
    public StackConfiguration withEmail(String userEmail) {
        this.userEmail = userEmail;
        return this;
    }
}
