package maquette.core.server.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedClassResolver;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.dsl.OpenApiBuilder;
import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.common.Operators;
import maquette.core.server.MaquetteServer;
import maquette.core.server.commands.Command;
import maquette.core.values.apidocs.*;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor()
public final class PostmanDocsResource {

   private static final Logger LOG = LoggerFactory.getLogger(MaquetteServer.class);

   MaquetteRuntime runtime;

   ObjectMapper om;

   /**
    * Generates a Postman collection including examples for all registered commands.
    *
    * @return The Javalin handler.
    */
   public Handler getDocs() {
      var docs = OpenApiBuilder
         .document()
         .operation(op -> {
            op.summary("Command Example");
            op.description("Get a postman collection with all available commands and examples.");
            op.addTagsItem("Commands");
         })
         .result("200");

      return OpenApiBuilder.documented(docs, ctx -> {
         var collection = Collection
            .apply("Maquette Hub API")
            .withVariable(Variable.apply("HOSTNAME", "localhost:9042"))
            .withVariable(Variable.apply("FILE", "some-file.bin"))
            .withVariable(Variable.apply("COLLECTION", "sample-collection"))
            .withVariable(Variable.apply("DATASET", "sample-dataset"))
            .withVariable(Variable.apply("SOURCE", "sample-source"))
            .withVariable(Variable.apply("REPOSITORY", "repository"))
            .withVariable(Variable.apply("REVISION", "af10-cc-fc-40"))
            .withVariable(Variable.apply("STREAM", "sample-stream"))
            .withVariable(Variable.apply("TAG", "tag"))
            .withVariable(Variable.apply("VERSION", "1.0"));

         getAvailableCommands().forEach(pair -> {
            var cmd = pair.getLeft();
            var command = pair.getRight();

            var json = Operators.suppressExceptions(() -> om.writeValueAsString(command.example()));

            var request = Request
               .apply()
               .withMethod(HttpMethod.POST)
               .withHeader("Content-Type", "application/json")
               .withHeader(runtime.getConfig().getCore().getUserIdHeaderName(), "alice")
               .withHeader(runtime.getConfig().getCore().getUserRolesHeaderName(), "a-team,b-team")
               .withHeader("x-project", "af10ccfc40")
               .withBody(RawBody.apply(json))
               .withUrl(Url.apply().withHost("{{HOSTNAME}}").withPath("api/commands"));

            collection.withItem(Item.apply("/api/commands - " + cmd, request));
         });

         ctx.json(collection);
      });
   }

   private List<Pair<String, Command>> getAvailableCommands() {
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
               LOG.warn("Unable to instantiate command `{}` for API docs - Does the command have a default constructor?", type.getName(), e);
               return Optional.<Pair<String, Command>>empty();
            }
         })
         .filter(Optional::isPresent)
         .map(Optional::get)
         .collect(Collectors.toList());
   }

}
