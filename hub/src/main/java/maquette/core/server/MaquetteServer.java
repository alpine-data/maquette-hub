package maquette.core.server;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import io.javalin.Javalin;
import io.javalin.http.Handler;
import io.javalin.plugin.json.JavalinJackson;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.config.ApplicationConfiguration;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.projects.ProjectEntity;
import maquette.core.server.results.MessageResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.UID;
import maquette.core.values.exceptions.DomainException;
import maquette.core.values.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AllArgsConstructor(staticName = "apply", access = AccessLevel.PRIVATE)
public final class MaquetteServer {

   private static final Logger LOG = LoggerFactory.getLogger(MaquetteServer.class);

   private final Javalin app;

   private final ApplicationConfiguration config;

   public static MaquetteServer apply(
      ApplicationConfiguration config,
      RuntimeConfiguration runtime,
      ApplicationServices services) {

      var om = runtime.getObjectMapper();
      runtime.getDataAssetProviders().toMap().values().forEach(provider -> {
         provider.getCustomCommands().forEach((key, command) -> {
            var type = new NamedType(command, String.format("%s %s", provider.getTypePluralized(), key));
            om.registerSubtypes(type);
         });
      });
      JavalinJackson.configure(om);

      var adminResource = new AdminResource(config);
      var commandResource = new CommandResource(runtime, services);
      var server = MaquetteServer.apply(runtime.getApp(), config);

      runtime.getApp()
         .before(server.handleAuthentication(
            config.getServer().getUserIdHeaderName(),
            config.getServer().getUserRolesHeaderName(),
            services, runtime))

         .post("/api/commands", commandResource.getCommand())
         .get("/api/commands", commandResource.getCommands())
         .get("/api/commands/examples", commandResource.getCommandExamples())

         .get("/api/about", adminResource.getAbout())
         .get("/api/about/user", adminResource.getUserInfo());

      runtime
         .getDataAssetProviders()
         .toMap()
         .values()
         .forEach(provider -> provider.configure(runtime.getApp(), config, runtime, services));

      runtime
         .getApp()
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

      return server;
   }

   private Handler handleAuthentication(
      String userIdHeaderName,
      String userRolesHeaderName,
      ApplicationServices services,
      RuntimeConfiguration runtime) {

      String userDetailsHeaderName = "x-user-details";
      String projectContextHeaderName = "x-project";
      String environmentContextHeaderName = "x-environment";

      return ctx -> {
         var headers = ctx.headerMap();

         if (headers.containsKey(userIdHeaderName)) {
            var userId = headers.get(userIdHeaderName);

            if (userId.equals(config.getServices().getKey())) { // TODO: User proper header keys for service user.
               var user = SystemUser.apply();
               ctx.attribute("user", user);
            } else {
               var user = AuthenticatedUser.apply(userId);

               if (headers.containsKey(userRolesHeaderName)) {
                  user = user.withRoles(headers.get(userRolesHeaderName).split(","));
               }

               if (headers.containsKey(userDetailsHeaderName)) {
                  var details = headers.get(userDetailsHeaderName);
                  services.getUserServices().updateUserDetails(user, details);
               }

               if (headers.containsKey(projectContextHeaderName)) {
                  var projectId = UID.apply(headers.get(projectContextHeaderName));

                  try {
                     var projectProperties = runtime
                        .getProjects()
                        .getProjectById(projectId)
                        .thenCompose(ProjectEntity::getProperties)
                        .toCompletableFuture()
                        .get();

                     user = user.withProjectContext(ProjectContext.apply(projectId, projectProperties));
                  } catch (Exception e) {
                     // Do nothing
                  }
               }

               if (headers.containsKey(environmentContextHeaderName)) {
                  try {
                     user = user.withEnvironmentContext(EnvironmentContext.fromString(headers.get(environmentContextHeaderName)));
                  } catch (Exception ex) {
                     LOG.warn("An error occurred reading the environment context of a request.", ex);
                  }
               }

               ctx.attribute("user", user);
            }
         } else {
            ctx.attribute("user", AnonymousUser.apply());
         }
      };
   }

   public void stop() {
      app.stop();
   }

}
