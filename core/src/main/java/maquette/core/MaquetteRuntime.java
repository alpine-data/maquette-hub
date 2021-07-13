package maquette.core;

import akka.japi.Function;
import com.google.common.collect.Lists;
import io.javalin.Javalin;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.config.MaquetteConfiguration;
import maquette.core.databind.DefaultObjectMapperFactory;
import maquette.core.databind.ObjectMapperFactory;
import maquette.core.modules.MaquetteModule;
import maquette.core.server.auth.AuthenticationHandler;
import maquette.core.server.auth.DefaultAuthenticationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

@Value
@AllArgsConstructor(staticName = "apply")
public class MaquetteRuntime {

   private static final Logger LOG = LoggerFactory.getLogger(Maquette.class);

   @With
   @Nullable
   Javalin app;

   @With
   MaquetteConfiguration config;

   @With
   ObjectMapperFactory objectMapperFactory;

   @With
   AuthenticationHandler authenticationHandler;

   List<Function<MaquetteRuntime, MaquetteModule>> moduleFactories;

   @With
   List<MaquetteModule> modules;

   public static MaquetteRuntime apply() {
      var cfg = MaquetteConfiguration.apply();
      var omf = DefaultObjectMapperFactory.apply();
      var ath = DefaultAuthenticationHandler.apply();

      return apply(null, cfg, omf, ath, List.of(), List.of());
   }

   @SuppressWarnings("unchecked")
   public <T extends MaquetteModule> T getModule(Class<T> type) {
      if (modules.size() == 0) {
         LOG.warn("Accessing getModule w/o registered modules. Are you calling the method before runtime is completely initialized?");
      }

      return modules
         .stream()
         .filter(type::isInstance)
         .map(m -> (T) m)
         .findFirst()
         .orElseThrow(() -> new IllegalStateException("The module of type {} has not been registered."));
   }

   public MaquetteRuntime withModule(Function<MaquetteRuntime, MaquetteModule> module) {
      var modulesNext = Lists.newArrayList(moduleFactories);
      modulesNext.add(module);

      return apply(app, config, objectMapperFactory, authenticationHandler, List.copyOf(modulesNext), modules);
   }

   public MaquetteRuntime withModule(MaquetteModule module) {
      var modulesNext = Lists.newArrayList(moduleFactories);
      modulesNext.add(mr -> module);
      return apply(app, config, objectMapperFactory, authenticationHandler, List.copyOf(modulesNext), modules);
   }

   public Javalin getApp() {
      if (Objects.isNull(app)) {
         throw new IllegalStateException("The runtime is not properly initialized yet.");
      }

      return app;
   }
}
