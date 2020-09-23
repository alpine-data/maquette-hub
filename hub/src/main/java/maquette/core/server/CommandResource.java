package maquette.core.server;

import com.fasterxml.jackson.databind.introspect.AnnotatedClassResolver;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.Handler;
import io.javalin.http.NotFoundResponse;
import io.javalin.plugin.openapi.dsl.OpenApiBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import maquette.common.ObjectMapperFactory;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor()
public final class CommandResource {

    private final RuntimeConfiguration runtime;

    private final ApplicationServices services;

    @AllArgsConstructor(staticName = "apply")
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private static class ExampleRequest {

        String name;

    }

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
                ctx.result(result.thenApply(r -> r.toPlainText(runtime)).toCompletableFuture());
            } else {
                ctx.json(result.toCompletableFuture());
            }
        });
    }

    public Handler getCommands() {
        var docs = OpenApiBuilder
           .document()
           .operation(op -> {
               op.summary("Commands");
               op.description("List available commands.");
               op.addTagsItem("Commands");
           })
           .jsonArray("200", String.class);

        return OpenApiBuilder.documented(docs, ctx -> {
           ctx.json(getAvailableCommands().stream().map(Pair::getLeft).sorted().collect(Collectors.toList()));
        });
    }

   public Handler getCommandExample() {
      var docs = OpenApiBuilder
         .document()
         .operation(op -> {
            op.summary("Command Example");
            op.description("Get an example for the command.");
            op.addTagsItem("Commands");
         })
         .body(ExampleRequest.class)
         .jsonArray("200", String.class);

      return OpenApiBuilder.documented(docs, ctx -> {
         var request = ctx.bodyAsClass(ExampleRequest.class);

         getAvailableCommands()
            .stream()
            .filter(pair -> pair.getLeft().equals(request.name))
            .findFirst()
            .ifPresentOrElse(
               pair -> ctx.json(pair.getRight().example()),
               () -> {
                  throw new NotFoundResponse(String.format("Unknown command `%s`", request.name));
               }
            );
      });
   }

    private List<Pair<String, Command>> getAvailableCommands() {
        var om = ObjectMapperFactory.apply().create(true);
        var ac = AnnotatedClassResolver.resolveWithoutSuperTypes(om.getDeserializationConfig(), Command.class);

        return om
           .getSubtypeResolver()
           .collectAndResolveSubtypesByClass(om.getDeserializationConfig(), ac)
           .stream()
           .filter(type -> !type.getType().isInterface())
           .map(type -> {
               try {
                   var constructor = type.getType().getDeclaredConstructor();
                   constructor.setAccessible(true);
                   var command = (Command) constructor.newInstance();
                   return Optional.of(Pair.of(type.getName(), command));
               } catch (Exception e) {
                   return Optional.<Pair<String, Command>>empty();
               }
           })
           .filter(Optional::isPresent)
           .map(Optional::get)
           .collect(Collectors.toList());
    }

}
