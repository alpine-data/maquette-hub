package maquette.core;

import akka.japi.Function;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.google.common.collect.Maps;
import io.javalin.Javalin;
import lombok.AllArgsConstructor;
import maquette.core.common.Templates;
import maquette.core.modules.MaquetteModule;
import maquette.core.server.MaquetteServer;
import maquette.core.server.resource.OpenApiResource;
import maquette.core.common.Operators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.stream.Collectors;

/**
 * Entrypoint for Maquette backend servers.
 */
@AllArgsConstructor(staticName = "apply")
public final class Maquette {

   private static final Logger LOG = LoggerFactory.getLogger(Maquette.class);

   private MaquetteRuntime runtime;

   @Nullable
   private MaquetteServer server;

   /**
    * Creates a new Maquette instance.
    *
    * @return The new instance.
    */
   public static Maquette apply() {
      var runtime = MaquetteRuntime.apply();
      return Maquette.apply(runtime, null);
   }

   /**
    * Configures the Maquette instance. This method should be called before
    * starting Maquette. After starting the instance, the function has now effect.
    *
    * @param cfg A function which modifies the current configuration.
    * @return The updated Maquette instance.
    */
   public Maquette configure(Function<MaquetteRuntime, MaquetteRuntime> cfg) {
      if (server != null) {
         throw new IllegalStateException("This method must be called before starting Maquette");
      }

      runtime = Operators.suppressExceptions(
         () -> cfg.apply(runtime),
         "An exception occurred while executing runtime configuration");

      return this;
   }

   /**
    * Start Maquette instance. This will start all services and run the webserver.
    *
    * @return The Maquette instance itself.
    */
   public Maquette start() {
      LOG.info("Starting Maquette Core ...");

      /*
       * Create server and initialize modules
       */
      var app = Javalin
         .create(config -> {
            config.showJavalinBanner = false;
            config.registerPlugin(OpenApiResource.apply(runtime.getConfig()));
         });

      runtime = runtime.withApp(app);

      /*
       * Initialize modules
       */
      runtime = runtime.withModules(runtime
         .getModuleFactories()
         .stream()
         .map(mf -> Operators.suppressExceptions(() -> mf.apply(runtime)))
         .collect(Collectors.toList()));

      runtime.getModules().forEach(module -> {
         LOG.info("Starting module {}", module.getName());
         module.start(runtime);
      });

      /*
       * Run server
       */
      server = MaquetteServer.apply(runtime);
      server.start();
      Runtime.getRuntime().addShutdownHook(new Thread(this::stop));

      var map = Maps.<String, Object>newHashMap();
      map.put("version", runtime.getConfig().getVersion());
      map.put("environment", runtime.getConfig().getEnvironment());
      var banner = Templates.renderTemplateFromResources("banner.twig", map);
      LOG.info("{} has started {}", runtime.getConfig().getName(), banner);

      return this;
   }

   /**
    * Gracefully stop Maquette.
    */
   public void stop() {
      LOG.info("Stopping Maquette ...");

      if (this.server != null) {
         this.server.stop();
         this.server = null;
      }

      runtime.getModules().forEach(module -> {
         LOG.info("Stopping module {}", module.getName());
         module.stop();
      });

      LOG.info("Maquette has stopped");
   }

}
