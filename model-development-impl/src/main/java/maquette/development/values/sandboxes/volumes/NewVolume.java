package maquette.development.values.sandboxes.volumes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.common.Operators;

/**
 * Requests to create a new volume.
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NewVolume implements VolumeDefinition {

    private static final String NAME = "name";

    @JsonProperty(NAME)
    String name;

    @JsonCreator
    public static NewVolume apply(@JsonProperty(NAME) String name) {
        if (!Operators.isKebabCase(name, 3)) {
            throw InvalidVolumeNameException.apply(name);
        }

        return new NewVolume(name);
    }

}
