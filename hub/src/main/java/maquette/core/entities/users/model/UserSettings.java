package maquette.core.entities.users.model;

import lombok.*;

import java.util.Optional;

@With
@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class UserSettings {

   GitSettings git;

   public static UserSettings apply() {
      return apply(null);
   }

   public Optional<GitSettings> getGit() {
      return Optional.ofNullable(git);
   }
}
