package maquette.core.entities.data;

import akka.Done;
import io.javalin.Javalin;
import maquette.core.config.ApplicationConfiguration;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.data.model.DataAssetProperties;
import maquette.core.server.Command;
import maquette.core.services.ApplicationServices;

import java.util.Map;
import java.util.concurrent.CompletionStage;

public interface DataAssetProvider {

   void configure(
      Javalin app, ApplicationConfiguration config, RuntimeConfiguration runtime, ApplicationServices services);

   Object getDefaultProperties();

   Class<?> getSettingsType();

   Class<?> getPropertiesType();

   String getType();

   String getTypePluralized();

   CompletionStage<Done> onCreated(DataAssetEntity entity, Object customSettings);

   CompletionStage<Done> onUpdatedCustomSettings(DataAssetEntity entity);

   CompletionStage<?> getDetails(DataAssetProperties properties, Object customSettings);

   Map<String, Class<? extends Command>> getCustomCommands();

}
