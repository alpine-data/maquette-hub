package maquette.core.values.authorization;

import maquette.core.common.exceptions.ApplicationException;

public final class Authorizations {

   private Authorizations() {

   }

   public static Authorization fromString(String authorizationType, String authorizationValue) {
      switch(authorizationType.toLowerCase()) {
         case "user":
         case "usr":
            return UserAuthorization.apply(authorizationValue);
         case "role":
            return RoleAuthorization.apply(authorizationValue);
         case "*":
         case "any":
         case "wildcard":
         case "?":
            return WildcardAuthorization.apply();
         default:
            throw ApplicationException.apply(
               "The provided authorization type `%s` is invalid. Valid types are `user`, `role` or `*`.",
               authorizationType);
      }
   }

}
