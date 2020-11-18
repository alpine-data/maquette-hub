package maquette.core.entities.sandboxes.model.stacks;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.common.Operators;

import java.net.URL;
import java.util.Map;

@Value
@AllArgsConstructor(staticName = "apply", access = AccessLevel.PRIVATE)
public class DeployedStackParameters {

   URL entrypoint;

   Map<String, Object> properties;

   @SuppressWarnings("unused")
   private DeployedStackParameters() {
      this(null, Maps.newHashMap());
   }

   public static DeployedStackParameters apply(URL entrypoint) {
      return apply(entrypoint, Maps.newHashMap());
   }

   public static DeployedStackParameters apply(String entrypoint) {
      return apply(Operators.suppressExceptions(() -> new URL(entrypoint)));
   }

   @JsonAnyGetter
   public Map<String, Object> getProperties() {
      return properties;
   }

   @JsonAnySetter
   public void setProperty(String name, Object value) {
      properties.put(name, value);
   }

}
