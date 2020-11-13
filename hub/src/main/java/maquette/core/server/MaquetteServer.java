package maquette.core.server;

import io.javalin.Javalin;
import io.javalin.http.Handler;
import io.javalin.plugin.json.JavalinJackson;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.core.config.ApplicationConfiguration;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.results.MessageResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.AnonymousUser;
import maquette.core.values.user.AuthenticatedUser;

@AllArgsConstructor(staticName = "apply", access = AccessLevel.PRIVATE)
public final class MaquetteServer {

    private final Javalin app;

    public static MaquetteServer apply(
            ApplicationConfiguration config,
            RuntimeConfiguration runtime,
            ApplicationServices services) {

        JavalinJackson.configure(runtime.getObjectMapper());

        var adminResource = new AdminResource(config);
        var commandResource = new CommandResource(runtime, services);
        var dataResource = new DataResource(services);

        runtime.getApp()
                .before(handleAuthentication(config.getServer().getUserIdHeaderName(), config.getServer().getUserRolesHeaderName()))

                .post("/api/commands", commandResource.getCommand())
                .get("/api/commands", commandResource.getCommands())
                .post("/api/commands/example", commandResource.getCommandExample())

                .post("/api/data/datasets/:project/:dataset/:revision", dataResource.upload())
                .get("/api/data/datasets/:project/:dataset/:version", dataResource.download())

                .get("/api/v1/about", adminResource.getAbout())
                .get("/api/v1/admin/user", adminResource.getUserInfo())

                .exception(Exception.class, (e, ctx) -> {
                    // TODO: Better error handling.
                    e.printStackTrace();
                    ctx.status(500);
                    ctx.json(MessageResult.apply(String.format("Internal Server Error: %s", e.getMessage())));
                });

        return apply(runtime.getApp());
    }

    private static Handler handleAuthentication(String userIdHeaderName, String userRolesHeaderName) {
        return ctx -> {
            var headers = ctx.headerMap();

            if (headers.containsKey(userIdHeaderName)) {
                var user = AuthenticatedUser.apply(headers.get(userIdHeaderName));

                if (headers.containsKey(userRolesHeaderName)) {
                    user = user.withRoles(headers.get(userRolesHeaderName).split(","));
                }

                ctx.attribute("user", user);
            } else {
                ctx.attribute("user", AnonymousUser.apply());
            }
        };
    }

    public void stop() {
        app.stop();
    }

}
