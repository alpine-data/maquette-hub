package maquette.core.server;

import io.javalin.http.Handler;
import io.javalin.plugin.openapi.dsl.OpenApiBuilder;
import lombok.AllArgsConstructor;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;

import java.util.Objects;

@AllArgsConstructor()
public final class CommandResource {

    private final RuntimeConfiguration runtime;

    private final ApplicationServices services;

    public Handler getCommand() {
        var docs = OpenApiBuilder
                .document()
                .operation(op -> {
                    op.summary("Commands");
                    op.description("Single endpoint to send commands to the application.");
                    op.addTagsItem("Commands");
                })
                .body(Command.class)
                .json("200", AdminResource.About.class);

        return OpenApiBuilder.documented(docs, ctx -> {
            var command = ctx.bodyAsClass(Command.class);
            var user = (User) Objects.requireNonNull(ctx.attribute("user"));
            var result = command.run(user, runtime, services).toCompletableFuture();

            var acceptRaw = ctx.header("Accept");
            var accept = acceptRaw != null ? acceptRaw : "application/json";

            if (accept.equals("text/plain")) {
                ctx.result(result.thenApply(r -> r.toPlainText(runtime)));
            } else {
                ctx.json(result);
            }
        });
    }

}
