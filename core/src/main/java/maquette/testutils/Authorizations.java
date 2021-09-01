package maquette.testutils;

import maquette.core.values.authorization.RoleAuthorization;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.authorization.WildcardAuthorization;

public final class Authorizations {

   public final UserAuthorization alice = UserAuthorization.apply("alice");

   public final UserAuthorization bob = UserAuthorization.apply("bob");

   public final UserAuthorization charly = UserAuthorization.apply("charly");

   public final WildcardAuthorization wildcard = WildcardAuthorization.apply();

   public final RoleAuthorization teamA = RoleAuthorization.apply("team-a");

   public final RoleAuthorization teamB = RoleAuthorization.apply("team-b");

   private Authorizations() {

   }

   public static Authorizations apply() {
      return new Authorizations();
   }

}
