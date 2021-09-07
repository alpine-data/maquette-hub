package maquette.core.modules.users.exceptions;

import maquette.core.common.exceptions.ApplicationException;
import maquette.core.values.user.User;

public final class MissingGitSettings extends ApplicationException {

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
