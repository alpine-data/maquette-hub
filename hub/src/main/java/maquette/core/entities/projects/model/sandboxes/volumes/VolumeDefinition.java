package maquette.core.entities.projects.model.sandboxes.volumes;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
   use = JsonTypeInfo.Id.NAME,
   property = "type")
@JsonSubTypes(
   {
      @JsonSubTypes.Type(value = ExistingVolume.class, name = "existing"),
      @JsonSubTypes.Type(value = GitVolume.class, name = "git"),
      @JsonSubTypes.Type(value = PlainVolume.class, name = "plain")
   })
public interface VolumeDefinition {
}
