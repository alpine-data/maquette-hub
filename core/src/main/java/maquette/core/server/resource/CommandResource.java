package maquette.core.server.resource;

import io.javalin.http.Handler;
import io.javalin.plugin.openapi.dsl.OpenApiBuilder;
import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.values.user.User;

import java.util.Objects;

@AllArgsConstructor()
public final class CommandResource {

    MaquetteRuntime runtime;

    /**
     * Handle execution of command requests.
     *
     * @return The Javalin handler.
     */
    public Handler getCommand() {
        var docs = OpenApiBuilder
            .document()
            .operation(op -> {
                op.summary("Commands");
                op.description("Single endpoint to send commands to the application.");
                op.addTagsItem("Commands");
            })
            .body(Command.class)
            .json("200", CommandResult.class);

        return OpenApiBuilder.documented(docs, ctx -> {
            var command = ctx.bodyAsClass(Command.class);
            var user = (User) Objects.requireNonNull(ctx.attribute("user"));

            var result = command
                .run(user, runtime)
                .toCompletableFuture();

            var acceptRaw = ctx.header("Accept");
            var accept = acceptRaw != null ? acceptRaw : "application/json";

            if (accept.equals("text/plain")) {
                ctx.result(result
                    .thenApply(r -> r.toPlainText(runtime))
                    .toCompletableFuture());
            } else if (accept.equals("application/csv")) {
                ctx.result(result
                    .thenApply(r -> r
                        .toCSV(runtime)
                        .orElseGet(() -> {
                            ctx.status(404);
                            return "CSV not available";
                        }))
                    .toCompletableFuture());
            } else {
                ctx.json(result.toCompletableFuture());
            }
        });
    }

}
