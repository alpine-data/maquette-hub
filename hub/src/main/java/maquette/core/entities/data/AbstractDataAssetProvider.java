package maquette.core.entities.data;

import akka.Done;
import com.google.common.collect.Maps;
import io.javalin.Javalin;
import maquette.core.config.ApplicationConfiguration;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.data.model.DataAssetProperties;
import maquette.core.server.Command;
import maquette.core.services.ApplicationServices;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public abstract class AbstractDataAssetProvider implements DataAssetProvider {

   private final String type;

   private final Class<?> propertiesType;

   private final Map<String, Class<? extends Command>> commands;

   public AbstractDataAssetProvider(String type, Class<?> propertiesType, Map<String, Class<? extends Command>> commands) {
      this.type = type;
      this.propertiesType = propertiesType;
      this.commands = commands;
   }

   public AbstractDataAssetProvider(String type, Class<?> propertiesType) {
      this(type, propertiesType, Maps.newHashMap());
   }

   public AbstractDataAssetProvider(String type) {
      this(type, Object.class);
   }

   protected void addCommand(String name, Class<? extends Command> cmd) {
      this.commands.put(name, cmd);
   }

   @Override
   public void configure(Javalin app, ApplicationConfiguration config, RuntimeConfiguration runtime, ApplicationServices services) {

   }

   @Override
   public Class<?> getSettingsType() {
      return propertiesType;
   }

   @Override
   public String getType() {
      return type;
   }

   @Override
   public String getTypePluralized() {
      return String.format("%ss", type);
   }

   @Override
   public CompletionStage<?> getDetails(DataAssetProperties properties, Object customSettings) {
      return CompletableFuture.completedFuture(null);
   }

   @Override
   public Map<String, Class<? extends Command>> getCustomCommands() {
      return Map.copyOf(commands);
   }

   @Override
   public CompletionStage<Done> onCreated(DataAssetEntity entity) {
      return CompletableFuture.completedFuture(Done.getInstance());
   }

}
