package maquette.core.server.auth;

import io.javalin.http.Context;
import maquette.core.MaquetteRuntime;
import maquette.core.values.user.User;
import maquette.core.modules.users.model.git.UserDetails;

import java.util.Optional;

public interface AuthenticationHandler {

   /**
    * Should return user authentication details, based on the request. Usually user information should have been injected
    * into the the request header.
    *
    * @param ctx The request context.
    * @param runtime The Maquette runtime instance.
    * @return The authenticated user object, might be {@link maquette.core.values.user.AnonymousUser} if no user information is included in request.
    */
   User handleAuthentication(Context ctx, MaquetteRuntime runtime);

   /**
    * User details might have been injected into the request as Base64 encoded JSON string. These user details
    * should be read here.
    *
    * @param ctx The request context.
    * @param runtime The Maquette runtime instance.
    * @return The user details, as Base64 encoded JSON string.
    */
   Optional<String> getUserDetailsFromRequest(Context ctx, MaquetteRuntime runtime);

}
