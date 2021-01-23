package maquette.sdk.config.authentication;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

import java.util.List;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class StupidAuthentication implements AuthenticationConfiguration {

   public static final String TYPE_KEY = "stupid";

   String username;

   List<String> roles;

   @SuppressWarnings("unused")
   private StupidAuthentication() {
      this.username = "alice";
      this.roles = List.of("a-team", "b-team");
   }

   public static StupidAuthentication apply() {
      return apply("alice", List.of("a-team", "b-team"));
   }

   @JsonIgnore
   public String getTypeKey() {
      return TYPE_KEY;
   }

}
