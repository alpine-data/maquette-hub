package maquette.core.entities.projects.ports.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public final class RegisteredModelsResponse {

   @JsonProperty("registered_models")
   private List<RegisteredModel> registeredModels;

}
