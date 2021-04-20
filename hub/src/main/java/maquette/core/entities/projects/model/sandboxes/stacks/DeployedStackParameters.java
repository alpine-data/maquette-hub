package maquette.core.entities.projects.model.sandboxes.stacks;

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

   String entrypointLabel;

   Map<String, Object> parameters;

   @SuppressWarnings("unused")
   private DeployedStackParameters() {
      this(null, null, Maps.newHashMap());
   }

   public static DeployedStackParameters apply(URL entrypoint, String entrypointLabel) {
      return apply(entrypoint, entrypointLabel, Maps.newHashMap());
   }

   public static DeployedStackParameters apply(String entrypoint, String entrypointLabel) {
      return apply(Operators.suppressExceptions(() -> new URL(entrypoint)), entrypointLabel);
   }

   @JsonAnyGetter
   public Map<String, Object> getParameters() {
      return Map.copyOf(parameters);
   }

   @JsonAnySetter
   public void setParameter(String name, Object value) {
      parameters.put(name, value);
   }

   public DeployedStackParameters withParameter(String name, String value) {
      setParameter(name, value);
      return this;
   }

}
