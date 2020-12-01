package maquette.core.server;

import io.javalin.Javalin;
import io.javalin.http.Handler;
import io.javalin.plugin.json.JavalinJackson;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.config.ApplicationConfiguration;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.results.MessageResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.exceptions.DomainException;
import maquette.core.values.user.AnonymousUser;
import maquette.core.values.user.AuthenticatedUser;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AllArgsConstructor(staticName = "apply", access = AccessLevel.PRIVATE)
public final class MaquetteServer {

    private static final Logger LOG = LoggerFactory.getLogger(MaquetteServer.class);

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

                .post("/api/data/datasets/:dataset", dataResource.uploadFile())
                .post("/api/data/datasets/:dataset/:revision", dataResource.upload())
                .get("/api/data/datasets/:dataset/:version", dataResource.download())

                .get("/api/v1/about", adminResource.getAbout())
                .get("/api/v1/admin/user", adminResource.getUserInfo())

                .exception(Exception.class, (e, ctx) -> {
                    var maybeDomainException = Operators.hasCause(e, DomainException.class);

                    if (maybeDomainException.isPresent()) {
                        var error = maybeDomainException.get();
                        ctx.status(error.getStatus());
                        ctx.json(MessageResult.apply(error.getMessage()));
                    } else {
                        LOG.warn("Unhandled exception upon API call", e);
                        ctx.status(500);
                        ctx.json(MessageResult.apply("Internal Server Error"));
                    }
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
