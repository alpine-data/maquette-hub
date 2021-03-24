package maquette.core.entities.data.assets_v2;

import io.javalin.Javalin;
import maquette.core.config.ApplicationConfiguration;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.data.assets_v2.model.DataAssetProperties;
import maquette.core.server.Command;
import maquette.core.services.ApplicationServices;

import java.util.Map;
import java.util.concurrent.CompletionStage;

public interface DataAssetProvider {

   void configure(
      Javalin app, ApplicationConfiguration config, RuntimeConfiguration runtime, ApplicationServices services);

   Class<?> getPropertiesType();

   String getType();

   String getTypePluralized();

   CompletionStage<?> getDetails(DataAssetProperties properties, Object customProperties);

   Map<String, Class<? extends Command>> getCustomCommands();

}
