package maquette.development.values.stacks;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.common.Operators;

import java.net.URL;
import java.util.Map;

/**
 * These values can be retrieved from a running stack instance. This may contain endpoint URLs, tool specific connection
 * parameters, secrets, etc...
 *
 * These parameters are usually required to connect or use the tools provided by a stack.
 */
@Value
@AllArgsConstructor(staticName = "apply", access = AccessLevel.PUBLIC)
public class StackInstanceParameters {

   URL entrypoint;

   String entrypointLabel;

   Map<String, String> parameters;

   @SuppressWarnings("unused")
   private StackInstanceParameters() {
      this(null, null, Maps.newHashMap());
   }

   public static StackInstanceParameters apply(URL entrypoint, String entrypointLabel) {
      return apply(entrypoint, entrypointLabel, Maps.newHashMap());
   }

   public static StackInstanceParameters apply(String entrypoint, String entrypointLabel) {
      return apply(Operators.suppressExceptions(() -> new URL(entrypoint)), entrypointLabel);
   }

   @JsonAnyGetter
   public Map<String, Object> getParameters() {
      return Map.copyOf(parameters);
   }

   @JsonAnySetter
   public void setParameter(String name, String value) {
      parameters.put(name, value);
   }

   public StackInstanceParameters withParameter(String name, String value) {
      setParameter(name, value);
      return this;
   }

}
