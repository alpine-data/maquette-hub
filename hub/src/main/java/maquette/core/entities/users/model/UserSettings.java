package maquette.core.entities.users.model;

import lombok.*;

@With
@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class UserSettings {

   GitSettings git;

   public static UserSettings apply() {
      return apply(GitSettings.apply());
   }

}
