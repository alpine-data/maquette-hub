package maquette.development.ports.infrastructure.docker.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MountedVolume {

    private static final String VOLUME = "volume";
    private static final String PATH = "path";

    @JsonProperty(VOLUME)
    DataVolume volume;

    @JsonProperty(PATH)
    String path;

    @JsonCreator
    public static MountedVolume apply(
        @JsonProperty(VOLUME) DataVolume volume,
        @JsonProperty(PATH) String path) {

        return new MountedVolume(volume, path);
    }

}
