package maquette.core.server;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import io.javalin.http.Context;
import io.javalin.plugin.json.JavalinJackson;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.core.Maquette;
import maquette.core.MaquetteRuntime;
import maquette.core.common.exceptions.ApplicationException;
import maquette.core.server.commands.MessageResult;
import maquette.core.server.resource.AdminResource;
import maquette.core.common.Operators;
import maquette.core.server.resource.CommandResource;
import maquette.core.server.resource.PostmanDocsResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class MaquetteServer {

   private static final Logger LOG = LoggerFactory.getLogger(Maquette.class);

   private final MaquetteRuntime runtime;

   /**
    * Creates a new Maquette server instance. This method configures all HTTP endpoints
    * of Maquette.
    *
    * @param runtime Maquette's runtime configuration.
    * @return The server instance.
    */
   public static MaquetteServer apply(MaquetteRuntime runtime) {
      var om = runtime.getObjectMapperFactory().createJsonMapper(true);
      JavalinJackson.configure(om);

      var adminResource = new AdminResource(runtime.getConfig());
      var docsResource = new PostmanDocsResource(runtime, om);
      var commandResource = new CommandResource(runtime);
      var server = new MaquetteServer(runtime);

      /*
       * Register commands from modules
       */
      runtime.getModules().forEach(module -> {
         module.getCommands().forEach((key, command) -> {
            var type = new NamedType(command, key);
            om.registerSubtypes(type);
         });
      });

      /*
       * Setup routes
       */
      runtime
         .getApp()
         .before(server::handleAuthentication)
         .get("/api/about", adminResource.getAbout())
         .get("/api/about/user", adminResource.getUserInfo())
         .get("/api/commands", docsResource.getDocs())
         .post("/api/commands", commandResource.getCommand());

      /*
       * Default exception handling
       */
      runtime
         .getApp()
         .exception(Exception.class, (e, ctx) -> {
            var maybeApplicationException = Operators.hasCause(e, ApplicationException.class);

            if (maybeApplicationException.isPresent()) {
               var error = maybeApplicationException.get();
               ctx.status(error.getHttpStatus());
               ctx.json(MessageResult.apply(error.getMessage()));
            } else {
               LOG.warn("Unhandled exception upon API call", e);
               ctx.status(500);
               ctx.json(MessageResult.apply("Internal Server Error"));
            }
         });

      return server;
   }

   private void handleAuthentication(Context ctx) {
      ctx.attribute("user", runtime.getAuthenticationHandler().handleAuthentication(ctx, runtime));
   }

   public void start() {
      runtime.getApp().start(
         runtime.getConfig().getCore().getHost(),
         runtime.getConfig().getCore().getPort());
   }

   public void stop() {
      runtime.getApp().stop();
   }

}
