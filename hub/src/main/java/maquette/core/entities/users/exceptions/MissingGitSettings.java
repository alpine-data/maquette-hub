package maquette.core.entities.users.exceptions;

import maquette.core.values.exceptions.DomainException;
import maquette.core.values.user.User;

public class MissingGitSettings extends RuntimeException implements DomainException  {

   private MissingGitSettings(String message) {
      super(message);
   }

   public static MissingGitSettings apply(User user) {
      var msg = String.format("No git settings configured for user `%s`.", user.getDisplayName());
      return new MissingGitSettings(msg);
   }

   public static MissingGitSettings apply() {
      var msg = "No git settings found in user settings.";
      return new MissingGitSettings(msg);
   }

}
