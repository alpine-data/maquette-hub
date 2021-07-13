package maquette.datashop.values;

import akka.Done;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.datashop.entities.DataAssetEntity;

import java.util.Map;
import java.util.concurrent.CompletionStage;

public interface DataAssetProvider {

   void configure(MaquetteRuntime runtime);

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
