package maquette.datashop.values.providers;

import akka.Done;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.datashop.entities.DataAssetEntity;
import maquette.datashop.values.DataAssetProperties;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * A fake data asset provider implementation w/o any function.
 */
@AllArgsConstructor(staticName = "apply")
public final class FakeProvider implements DataAssetProvider {

   public static final String NAME = "fake";

   @Override
   public void configure(MaquetteRuntime runtime) {

   }

   @Override
   public Object getDefaultProperties() {
      return new Object();
   }

   @Override
   public Class<?> getSettingsType() {
      return Object.class;
   }

   @Override
   public Class<?> getPropertiesType() {
      return Object.class;
   }

   @Override
   public String getType() {
      return NAME;
   }

   @Override
   public String getTypePluralized() {
      return NAME + "s";
   }

   @Override
   public CompletionStage<Done> onCreated(DataAssetEntity entity, Object customSettings) {
      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<Done> onUpdatedCustomSettings(DataAssetEntity entity) {
      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<?> getDetails(DataAssetProperties properties, Object customSettings) {
      return CompletableFuture.completedFuture(new Object());
   }

   @Override
   public Map<String, Class<? extends Command>> getCustomCommands() {
      return Maps.newHashMap();
   }

}
