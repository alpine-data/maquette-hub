package maquette.core.entities.users.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.apache.commons.codec.digest.DigestUtils;

@With
@Value
@AllArgsConstructor(staticName = "apply")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfile {

   String id;

   String name;

   String title;

   String bio;

   String email;

   String phone;

   String location;

   @SuppressWarnings("unused")
   private UserProfile() {
      this("", "", "", "", "", "", "");
   }

   public static UserProfile apply(String id) {
      return apply(id, "", "", "", "", "", "");
   }

   @JsonProperty("avatar")
   public String getProfilePicture() {
      return "https://www.gravatar.com/avatar/" + DigestUtils.md5Hex(email != null ? email : "foo@bar.de") + "?d=retro";
   }

}
