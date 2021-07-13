package maquette.core.server.auth;

import io.javalin.http.Context;
import maquette.core.MaquetteRuntime;
import maquette.core.values.user.User;

public interface AuthenticationHandler {

   User handleAuthentication(Context ctx, MaquetteRuntime runtime);

}
