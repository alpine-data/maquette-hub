package maquette.sdk.config;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Optional;

@Value
@AllArgsConstructor(staticName = "apply")
public class ProjectConfiguration {

   String name;

   String id;

   @SuppressWarnings("unsed")
   private ProjectConfiguration() {
      this.name = "";
      this.id = "";
   }

   public static ProjectConfiguration apply() {
      return apply("", "");
   }

   public ProjectConfiguration withEnvironmentOverrides() {
      var result = this;

      result = Optional
         .ofNullable(System.getenv("MQ_PROJECT_NAME"))
         .map(name -> apply(name, id))
         .orElse(result);

      result = Optional
         .ofNullable(System.getenv("MQ_PROJECT_ID"))
         .map(id -> apply(name, id))
         .orElse(result);

      return result;
   }

}
