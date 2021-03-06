package maquette.development.values.sandboxes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.development.values.stacks.StackRuntimeState;

import java.util.List;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Sandbox {

    private static final String PROPERTIES = "properties";
    private static final String STACKS = "stacks";

    /**
     * Sandbox properties and details.
     */
    @JsonProperty(PROPERTIES)
    SandboxProperties properties;

    /**
     * Runtime information from stacks.
     */
    @JsonProperty(STACKS)
    List<StackRuntimeState> stacks;

    /**
     * Creates a new instance.
     *
     * @param properties Sandbox properties and details.
     * @param stacks     Runtime information from stacks.
     * @return A new sandbox instance.
     */
    @JsonCreator
    public static Sandbox apply(
        @JsonProperty(PROPERTIES) SandboxProperties properties,
        @JsonProperty(STACKS) List<StackRuntimeState> stacks) {

        return new Sandbox(properties, stacks);
    }

}
