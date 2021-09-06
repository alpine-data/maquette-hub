package maquette.core.modules.users.model;

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
      return new GitSettings();
   }

   public boolean isEmpty() {
      return (username == null || username.trim().isEmpty())
         && (password == null || password.trim().isEmpty())
         && (privateKey == null || privateKey.trim().isEmpty())
         && (publicKey == null || publicKey.trim().isEmpty());
   }

}
