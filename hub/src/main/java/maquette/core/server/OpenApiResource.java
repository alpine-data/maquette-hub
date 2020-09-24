package maquette.core.server;

import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.swagger.v3.oas.models.info.Info;
import maquette.core.config.ApplicationConfiguration;

public final class OpenApiResource {

   private OpenApiResource() {

   }

   public static OpenApiPlugin apply(ApplicationConfiguration config) {
      var info = new Info()
         .version(config.getVersion())
         .title("Maquette")
         .description("Maquette REST API.");

      var options = new OpenApiOptions(info)
         .path("/api")
         .swagger(new SwaggerOptions("/api/docs").title("Maquette Open API Documentation"));

      return new OpenApiPlugin(options);
   }

}
