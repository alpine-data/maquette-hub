package maquette.development.values.sandboxes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.development.values.stacks.StackConfiguration;

import java.util.Map;
import java.util.Optional;

@With
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SandboxProperties {

    private static final String ID = "id";
    private static final String COMMENT = "comment";
    private static final String WORKSPACE = "workspace";
    private static final String VOLUME = "volume";
    private static final String NAME = "name";
    private static final String CREATED = "created";
    private static final String STACKS = "stacks";

    /**
     * A unique id for the sandbox. The id is unique across all sandboxes of the system.
     */
    @JsonProperty(ID)
    UID id;

    /**
     * The id of the workspace the sandbox belongs to.
     */
    @JsonProperty(WORKSPACE)
    UID workspace;

    /**
     * The id of the volume which is bound to the sandbox.
     */
    @JsonProperty(VOLUME)
    UID volume;

    /**
     * The name of the sandbox.
     */
    @JsonProperty(NAME)
    String name;

    /**
     * A comment describing the purpose of the sandbox.
     */
    @JsonProperty(COMMENT)
    String comment;

    /**
     * Creation metadata, the creator of the sandbox is also its owner.
     */
    @JsonProperty(CREATED)
    ActionMetadata created;

    /**
     * A list of stack ids which belong to this sandbox.
     */
    @JsonProperty(STACKS)
    Map<String, StackConfiguration> stacks;

    /**
     * Creates a new instance.
     *
     * @param id        A unique id for the sandbox. The id is unique across all sandboxes of the system.
     * @param workspace The id of the workspace the sandbox belongs to.
     * @param volume    The id of the volume which is bound to the sandbox.
     * @param name      The name of the sandbox.
     * @param created   Creation metadata, the creator of the sandbox is also its owner.
     * @param stacks    A list of stack ids which belong to this sandbox.
     * @return A new instance.
     */
    @JsonCreator
    public static SandboxProperties apply(
        @JsonProperty(ID) UID id,
        @JsonProperty(WORKSPACE) UID workspace,
        @JsonProperty(VOLUME) UID volume,
        @JsonProperty(NAME) String name,
        @JsonProperty(COMMENT) String comment,
        @JsonProperty(CREATED) ActionMetadata created,
        @JsonProperty(STACKS) Map<String, StackConfiguration> stacks) {

        return new SandboxProperties(id, workspace, volume, name, comment, created, stacks);
    }

    public static SandboxProperties apply(UID id, UID workspace, UID volume, String name, String comment,
                                          ActionMetadata created) {

        return apply(id, workspace, volume, name, comment, created, Maps.newHashMap());
    }

    public SandboxProperties withAdditionalStacks(Map<String, StackConfiguration> stacks) {
        var stacksUpdated = Maps.<String, StackConfiguration>newHashMap();
        stacksUpdated.putAll(this.stacks);
        stacksUpdated.putAll(stacks);

        return withStacks(stacksUpdated);
    }

    public Optional<UID> getVolume() {
        return Optional.ofNullable(volume);
    }

}
