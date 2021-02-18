package maquette.core.server;

import io.javalin.Javalin;
import io.javalin.http.Handler;
import io.javalin.plugin.json.JavalinJackson;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.config.ApplicationConfiguration;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.results.MessageResult;
import maquette.core.services.ApplicationServices;
import maquette.core.services.users.UserServices;
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

      JavalinJackson.configure(runtime.getObjectMapper());

      var adminResource = new AdminResource(config);
      var commandResource = new CommandResource(runtime, services);
      var dataResource = new DataResource(services);
      var collectionDataResource = new CollectionDataResource(services);

      var server = MaquetteServer.apply(runtime.getApp(), config);

      runtime.getApp()
         .before(server.handleAuthentication(
            config.getServer().getUserIdHeaderName(),
            config.getServer().getUserRolesHeaderName(),
            services.getUserServices()))

         .post("/api/commands", commandResource.getCommand())
         .get("/api/commands", commandResource.getCommands())
         .get("/api/commands/examples", commandResource.getCommandExamples())

         .post("/api/data/collections/:collection", collectionDataResource.upload())
         .get("/api/data/collections/:collection/latest", collectionDataResource.download())
         .get("/api/data/collections/:collection/tags/:tag", collectionDataResource.download())
         .get("/api/data/collections/:collection/latest/*", collectionDataResource.downloadFile())
         .get("/api/data/collections/:collection/tags/:tag/*", collectionDataResource.downloadFile())
         .delete("/api/data/collections/:collection/latest/*", collectionDataResource.remove())

         .post("/api/data/datasets/:dataset", dataResource.uploadDatasetFile())
         .post("/api/data/datasets/:dataset/:revision", dataResource.upload())
         .get("/api/data/datasets/:dataset/:version", dataResource.downloadDatasetVersion())
         .get("/api/data/datasets/:dataset", dataResource.downloadLatestDatasetVersion())
         .get("/api/data/sources/:source", dataResource.downloadDatasource())

         .get("/api/about", adminResource.getAbout())
         .get("/api/about/user", adminResource.getUserInfo())

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

   private Handler handleAuthentication(String userIdHeaderName, String userRolesHeaderName, UserServices userServices) {
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
                  userServices.updateUserDetails(user, details);
               }

               if (headers.containsKey(projectContextHeaderName)) {
                  user = user.withProjectContext(ProjectContext.apply(UID.apply(headers.get(projectContextHeaderName))));
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
