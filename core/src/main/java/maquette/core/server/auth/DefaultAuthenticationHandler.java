package maquette.core.server.auth;

import com.google.common.collect.Lists;
import io.javalin.http.Context;
import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.values.UID;
import maquette.core.values.user.AnonymousUser;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public class DefaultAuthenticationHandler implements AuthenticationHandler {

    @Override
    public User handleAuthentication(Context ctx, MaquetteRuntime runtime) {
        var userIdHeaderName = runtime
            .getConfig()
            .getCore()
            .getUserIdHeaderName();
        var userRolesHeaderName = runtime
            .getConfig()
            .getCore()
            .getUserRolesHeaderName();
        var headers = ctx.headerMap();
        var roles = (List<String>) Lists.<String>newArrayList();

        if (headers.containsKey(userRolesHeaderName)) {
            roles = Arrays
                .stream(headers
                    .get(userRolesHeaderName)
                    .split(","))
                .map(String::trim)
                .collect(Collectors.toList());
        }

        if (headers.containsKey(userIdHeaderName)) {
            var userId = headers.get(userIdHeaderName);
            return AuthenticatedUser.apply(UID.apply(userId), roles);
        } else {
            return AnonymousUser.apply(roles);
        }
    }

    @Override
    public Optional<String> getUserDetailsFromRequest(Context ctx, MaquetteRuntime runtime) {
        var headers = ctx.headerMap();
        var userDetailsHeaderName = runtime
            .getConfig()
            .getCore()
            .getUserDetailsHeaderName();

        if (headers.containsKey(userDetailsHeaderName)) {
            return Optional.ofNullable(headers.get(userDetailsHeaderName));
        } else {
            return Optional.empty();
        }
    }

}
