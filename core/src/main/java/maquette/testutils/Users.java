package maquette.testutils;

import maquette.core.values.UID;
import maquette.core.values.user.AnonymousUser;
import maquette.core.values.user.AuthenticatedUser;

public final class Users {

   public final AuthenticatedUser alice = AuthenticatedUser.apply(UID.apply("alice"), "team-a", "team-b");

   public final AuthenticatedUser bob = AuthenticatedUser.apply(UID.apply("bob"), "team-a");

   public final AuthenticatedUser charly = AuthenticatedUser.apply(UID.apply("charly"), "team-b");

   public final AnonymousUser anonymous = AnonymousUser.apply();

   private Users() {

   }

   public static Users apply() {
      return new Users();
   }

}
