package maquette.development.values.stacks;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.Maps;
import maquette.core.values.UID;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Custom configuration types for defined stacks. See also {@link Stack}.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "stack")
@JsonSubTypes({
    @JsonSubTypes.Type(value = MlflowStackConfiguration.class, name = MlflowStack.STACK_NAME),
    @JsonSubTypes.Type(value = PostgresStackConfiguration.class, name = PostgresStack.STACK_NAME),
    @JsonSubTypes.Type(value = PythonStackConfiguration.class, name = PythonStack.STACK_NAME),
    @JsonSubTypes.Type(value = PythonGPUStackConfiguration.class, name = PythonGPUStack.STACK_NAME),
    @JsonSubTypes.Type(value = SynapseStackConfiguration.class, name = SynapseStack.STACK_NAME),
    @JsonSubTypes.Type(value = MLWorkspaceStackConfiguration.class, name = MLWorkspaceStack.STACK_NAME)
})
public interface StackConfiguration {

    /**
     * This parameter might be set by a stack and returned with
     * {@link maquette.development.ports.infrastructure.InfrastructurePort#getInstanceParameters(UID, String)}.
     * It's a unique secret hash only known by the stack instance and can be used for authentication.
     */
    String PARAM_STACK_TOKEN = "MQ_STACK_TOKEN";

    /**
     * An instance name of the stack. This name must be unique across all stacks managed by the infrastructure port.
     *
     * @return The name of the stack.
     */
    String getStackInstanceName();

    /**
     * A list of MARS managed resource groups which are related to this MLflow instance.
     * Might be used for tagging of resources, or creation of private networks between the groups.
     */
    List<String> getResourceGroups();

    /**
     * Environment variables which are set as input from Mars Hub which should set on all nodes of the stack.
     *
     * @return A map with environment variables.
     */
    Map<String, String> getEnvironmentVariables();

    /**
     * Returns the E-Mail address of the user who owns the sandbox. Might not always be provided.
     *
     * @return The user email, if present.
     */
    Optional<String> getUserEmail();

    /**
     * Returns whether the stack is to be secured with oauth2 proxy
     *
     * @return True if stack is to be secured
     */
    boolean isSecured();

    /**
     * Provides a volume ID for stack that will be mounted.
     *
     * @return A volume ID if a stack supports it, otherwise empty
     */
    Optional<UID> getVolume();

    /**
     * Returns a copy of the stack configuration, with updated name. This is required, since the backend
     * sets the instance name.
     *
     * @param name The new name.
     * @return A copy of the {@link StackConfiguration}
     */
    StackConfiguration withStackInstanceName(String name);

    /**
     * Return this stack configuration with additional environment variables.
     *
     * @param environment The new environment variables.
     * @return Updated stack configuration.
     */
    StackConfiguration withEnvironmentVariables(Map<String, String> environment);

    /**
     * Add a single entry to the environment variable.
     *
     * @param key   The environment variable name.
     * @param value The environment variable value.
     * @return A new instance of this configuration with updated environment.
     */
    default StackConfiguration withEnvironmentVariable(String key, String value) {
        var updated = Maps.<String, String>newHashMap();
        updated.putAll(getEnvironmentVariables());
        updated.put(key, value);
        return withEnvironmentVariables(updated);
    }

    /**
     * Add a volume ID to the stack.
     *
     * @param volume
     * @return A new instance of this configuration with updated volume
     */
    StackConfiguration withVolume(UID volume);

    /**
     * Add a configured cost to the stack.
     *
     * @param cost
     * @return A new instance of this configuration with updated cost
     */
    StackConfiguration withCost(double cost);

    /**
     * Add the email of the user who owns this stack.
     *
     * This might be used for authentication purposes on the sandbox.
     *
     * @param userEmail The email address of the user.
     * @return Stack config with updated userEmail.
     */
    StackConfiguration withEmail(String userEmail);

}
