package maquette.core.server;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import io.javalin.http.Context;
import io.javalin.plugin.json.JavalinJackson;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.core.Maquette;
import maquette.core.MaquetteRuntime;
import maquette.core.common.exceptions.ApplicationException;
import maquette.core.modules.users.UserModule;
import maquette.core.server.commands.MessageResult;
import maquette.core.server.resource.AdminResource;
import maquette.core.common.Operators;
import maquette.core.server.resource.CommandResource;
import maquette.core.server.resource.PostmanDocsResource;
import maquette.core.values.user.AnonymousUser;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

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

      var adminResource = new AdminResource(runtime);
      var docsResource = new PostmanDocsResource(runtime, om);
      var commandResource = new CommandResource(runtime);
      var server = new MaquetteServer(runtime);

      /*
       * Register commands from modules
       */
      runtime.getModules().forEach(module -> module.getCommands().forEach((key, command) -> {
         var type = new NamedType(command, key);
         om.registerSubtypes(type);
      }));

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
      /*
       * use custom authentication handler
       */
      ctx.attribute("user", runtime.getAuthenticationHandler().handleAuthentication(ctx, runtime));

      /*
       * handle user details if present in request
       */
      var user = (User) ctx.attribute("user");
      var userDetails = runtime.getAuthenticationHandler().getUserDetailsFromRequest(ctx, runtime);

      if (userDetails.isPresent() && user instanceof AuthenticatedUser) {
         Operators.ignoreExceptions(() -> runtime
            .getModule(UserModule.class)
            .getServices()
            .updateUserDetails(ctx.attribute("user"), userDetails.get())
            .toCompletableFuture()
            .get());
      }

      /*
       * handle technical authentication tokens
       */
      var headers = ctx.headerMap();

      if (user instanceof AnonymousUser
         && headers.containsKey(runtime.getConfig().getCore().getAuthTokenSecretHeaderName())
         && headers.containsKey(runtime.getConfig().getCore().getAuthTokenIdHeaderName())) {

         var tokenId = headers.get(runtime.getConfig().getCore().getAuthTokenIdHeaderName());
         var tokenSecret = headers.get(runtime.getConfig().getCore().getAuthTokenSecretHeaderName());

         var maybeAuthUser = Operators.<Optional<AuthenticatedUser>>ignoreExceptionsWithDefault(() -> runtime
            .getModule(UserModule.class)
            .getServices()
            .getUserForAuthenticationToken(tokenId, tokenSecret)
            .toCompletableFuture()
            .get(), Optional.empty());

         maybeAuthUser.ifPresent(authUser -> ctx.attribute("user", maybeAuthUser.get()));
      }
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
