package maquette.core.server.resource;

import io.javalin.http.Handler;
import io.javalin.plugin.openapi.dsl.OpenApiBuilder;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.config.MaquetteConfiguration;
import maquette.core.values.user.User;

import java.util.Objects;

@AllArgsConstructor
public final class AdminResource {

    MaquetteConfiguration config;

    @Value
    @AllArgsConstructor(staticName = "apply")
    public static class About {

        String environment;

        String version;

    }

    public Handler getAbout() {
        var docs = OpenApiBuilder
                .document()
                .operation(op -> {
                    op.summary("Application Info");
                    op.description("Returns basic meta information of the application.");
                    op.addTagsItem("Admin");
                })
                .json("200", About.class);

        return OpenApiBuilder.documented(docs, ctx -> {
            ctx.json(About.apply(config.getEnvironment(), config.getVersion()));
        });
    }

    public Handler getUserInfo() {
        var docs = OpenApiBuilder
                .document()
                .operation(op -> {
                    op.summary("User Info");
                    op.description("Returns user information from the authenticated user.");
                    op.addTagsItem("Admin");
                })
                .json("200", User.class);

        return OpenApiBuilder.documented(docs, ctx -> {
            ctx.json(Objects.requireNonNull(ctx.attribute("user")));
        });
    }

}
