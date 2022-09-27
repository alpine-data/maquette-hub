package maquette.development.values.stacks;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.UID;

@With
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VolumeConfiguration {

    private static final String ID = "id";

    private static final String USER = "user";

    private static final String NAME = "name";

    private static final String SIZE = "size";

    /**
     * Unique id.
     */
    @JsonProperty(ID)
    UID id;

    /**
     * User to which a volume belongs.
     */
    @JsonProperty(USER)
    UID user;

    /**
     * The name of the volume displayed to the user.
     */
    @JsonProperty(NAME)
    String name;

    /**
     * Size of a volume in the following format
     * https://kubernetes.io/docs/reference/kubernetes-api/common-definitions/quantity/
     */
    @JsonProperty(SIZE)
    String size;

    @JsonCreator
    public static VolumeConfiguration apply(@JsonProperty(ID) UID id, @JsonProperty(USER) UID user,
                                            @JsonProperty(NAME) String name, @JsonProperty(SIZE) String size) {
        return new VolumeConfiguration(id, user, name, size);
    }
}
