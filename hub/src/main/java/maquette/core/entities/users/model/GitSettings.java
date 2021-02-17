package maquette.core.entities.users.model;

import lombok.*;

@With
@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class GitSettings {

   String username;

   String password;

   String privateKey;

   String publicKey;

   public static GitSettings apply() {
      return apply("", "", "", "");
   }

}
