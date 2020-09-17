package maquette.core.server;

import io.javalin.Javalin;
import io.javalin.http.Handler;
import io.javalin.plugin.json.JavalinJackson;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.core.api.Projects;
import maquette.core.config.ApplicationConfiguration;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.values.user.AnonymousUser;
import maquette.core.values.user.AuthenticatedUser;

@AllArgsConstructor(staticName = "apply", access = AccessLevel.PRIVATE)
public final class MaquetteServer {

    private final Javalin app;

    public static MaquetteServer apply(
            ApplicationConfiguration config,
            RuntimeConfiguration runtime,
            Projects projects) {

        JavalinJackson.configure(runtime.getObjectMapper());

        var adminResource = new AdminResource(config);
        var projectsResource = new ProjectsResource(projects);

        runtime.getApp()
                .before(handleAuthentication(config.getServer().getUserIdHeaderName(), config.getServer().getUserRolesHeaderName()))
                .get("/api/v1/projects", projectsResource.getProjects())

                .get("/api/v1/about", adminResource.getAbout())
                .get("/api/v1/admin/user", adminResource.getUserInfo());

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
