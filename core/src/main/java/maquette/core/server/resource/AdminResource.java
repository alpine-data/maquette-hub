package maquette.core.server.resource;

import io.javalin.http.Handler;
import io.javalin.plugin.openapi.dsl.OpenApiBuilder;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.modules.users.UserModule;
import maquette.core.modules.users.model.UserAuthenticationToken;
import maquette.core.values.user.User;

@AllArgsConstructor
public final class AdminResource {

    MaquetteRuntime runtime;

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
            ctx.json(About.apply(runtime.getConfig().getEnvironment(), runtime.getConfig().getVersion()));
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
            var services = runtime.getModule(UserModule.class).getServices();
            var user = (User) ctx.attribute("user");

            var result = services
                .getAuthenticationToken(user)
                .thenApply(token -> UserInformation.apply(user, token))
                .toCompletableFuture();

            ctx.json(result);
        });
    }

    @Value
    @AllArgsConstructor(staticName = "apply")
    public static class UserInformation {

        User user;

        UserAuthenticationToken token;

    }

}
