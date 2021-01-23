package maquette.sdk.config;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Optional;

@Value
@AllArgsConstructor(staticName = "apply")
public class EnvironmentConfiguration {

   String id;

   String type;

   @SuppressWarnings("unused")
   private EnvironmentConfiguration() {
      this.id = "0";
      this.type = "local";
   }

   public static EnvironmentConfiguration apply() {
      return apply("0", "local");
   }

   public EnvironmentConfiguration withEnvironmentOverrides() {
      var result = this;

      result = Optional
         .ofNullable(System.getenv("MQ_ENVIRONMENT_ID"))
         .map(id -> apply(id, type))
         .orElse(result);

      result = Optional
         .ofNullable(System.getenv("MQ_ENVIRONMENT_TYPE"))
         .map(type -> apply(id, type))
         .orElse(result);

      return result;
   }

}
