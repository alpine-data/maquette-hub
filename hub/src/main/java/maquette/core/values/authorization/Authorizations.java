package maquette.core.values.authorization;

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
            // TODO mw: better error handling
            throw new RuntimeException("Invalid authorization type");
      }
   }

}
