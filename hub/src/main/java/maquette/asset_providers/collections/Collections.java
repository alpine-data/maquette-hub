package maquette.asset_providers.collections;

import io.javalin.Javalin;
import maquette.asset_providers.collections.commands.CreateCollectionTagCommand;
import maquette.asset_providers.collections.commands.ListCollectionFilesCommand;
import maquette.asset_providers.collections.model.CollectionDetails;
import maquette.asset_providers.collections.services.CollectionServices;
import maquette.asset_providers.collections.services.CollectionServicesFactory;
import maquette.common.Operators;
import maquette.core.config.ApplicationConfiguration;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.data.AbstractDataAssetProvider;
import maquette.core.entities.data.model.DataAssetProperties;
import maquette.core.services.ApplicationServices;

import java.util.concurrent.CompletionStage;

public final class Collections extends AbstractDataAssetProvider {

   public static String TYPE_NAME = "collection";

   private final CollectionsRepository repository;

   private Collections(CollectionsRepository repository) {
      super(TYPE_NAME);
      this.repository = repository;
   }

   public static Collections apply(CollectionsRepository repository) {
      var collections = new Collections(repository);

      collections.addCommand("tag", CreateCollectionTagCommand.class);
      collections.addCommand("list", ListCollectionFilesCommand.class);

      return collections;
   }

   @Override
   public void configure(Javalin app, ApplicationConfiguration config, RuntimeConfiguration runtime, ApplicationServices services) {
      super.configure(app, config, runtime, services);
      var handler = new CollectionsAPI(getServices(runtime));

      app
         .post("/api/data/collections/:collection", handler.upload())
         .get("/api/data/collections/:collection/latest", handler.download())
         .get("/api/data/collections/:collection/tags/:tag", handler.download())
         .get("/api/data/collections/:collection/latest/*", handler.downloadFile())
         .get("/api/data/collections/:collection/tags/:tag/*", handler.downloadFile())
         .delete("/api/data/collections/:collection/latest/*", handler.remove());
   }

   @Override
   public CompletionStage<?> getDetails(DataAssetProperties properties, Object customProperties) {
      var filesCS = repository.getFiles(properties.getId());
      var tagsCS = repository.findAllTags(properties.getId());

      return Operators.compose(filesCS, tagsCS, CollectionDetails::apply);
   }

   public CollectionServices getServices(RuntimeConfiguration runtime) {
      return CollectionServicesFactory.create(runtime, repository);
   }

}
