package maquette.core.entities.projects.ports.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public final class RegisteredModel {

   private String name;

   @JsonProperty("creation_timestamp")
   private long creationTimestamp;

   @JsonProperty("last_updated_timestamp")
   private long lastUpdatedTimestamp;

}
