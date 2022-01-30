package maquette.development.values.stacks;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import maquette.development.values.EnvironmentType;

import java.util.List;
import java.util.Map;

/**
 * Custom configuration types for defined stacks. See also {@link Stack}.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "stack")
@JsonSubTypes({
    @JsonSubTypes.Type(value = DummyPythonStackConfiguration.class, name = DummyPythonStack.STACK_NAME),
    @JsonSubTypes.Type(value = MlflowStackConfiguration.class, name = MlflowStack.STACK_NAME),
    @JsonSubTypes.Type(value = PostgresStackConfiguration.class, name = PostgresStack.STACK_NAME),
    @JsonSubTypes.Type(value = SynapseStackConfiguration.class, name = SynapseStack.STACK_NAME)
})
public interface StackConfiguration {

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
     * Return parameters/ properties which are dependant on runtime-parameters of the stack.
     *
     * @param parameters A set of runtime parameters retrieved from infrastructure port.
     * @param environment
     * @return The stacks parameters/ properties.
     */
    StackInstanceParameters getInstanceParameters(Map<String, String> parameters, EnvironmentType environment);

    /**
     * Returns a copy of the stack configuration, with updated name. This is required, since the backend
     * sets the instance name.
     *
     * @param name The new name.
     * @return A copy of the {@link StackConfiguration}
     */
    StackConfiguration withStackInstanceName(String name);

}
