package maquette.development.values.sandboxes.volumes;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * A volume definition is used when creating Sandboxes. It defines whether, and if yes which, volume should be
 * linked to a sandbox.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type")
@JsonSubTypes(
    {
        @JsonSubTypes.Type(value = ExistingVolume.class, name = "existing"),
        @JsonSubTypes.Type(value = NewVolume.class, name = "existing")
    })
public interface VolumeDefinition {

}
