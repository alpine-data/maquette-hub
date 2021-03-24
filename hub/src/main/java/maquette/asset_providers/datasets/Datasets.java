package maquette.asset_providers.datasets;

import io.javalin.Javalin;
import maquette.asset_providers.datasets.commands.CommitRevisionCommand;
import maquette.asset_providers.datasets.commands.CreateRevisionCommand;
import maquette.asset_providers.datasets.services.DatasetServices;
import maquette.asset_providers.datasets.services.DatasetServicesFactory;
import maquette.core.config.ApplicationConfiguration;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.data.assets_v2.AbstractDataAssetProvider;
import maquette.core.entities.data.assets_v2.model.DataAssetProperties;
import maquette.core.ports.DataExplorer;
import maquette.core.ports.RecordsStore;
import maquette.core.services.ApplicationServices;

import java.util.concurrent.CompletionStage;

public final class Datasets extends AbstractDataAssetProvider {

   public static final String TYPE_NAME = "dataset";

   private final DatasetsRepository repository;

   private final RecordsStore recordsStore;

   private final DataExplorer dataExplorer;

   private Datasets(DatasetsRepository repository, RecordsStore recordsStore, DataExplorer dataExplorer) {
      super(TYPE_NAME);
      this.repository = repository;
      this.recordsStore = recordsStore;
      this.dataExplorer = dataExplorer;
   }


   public static Datasets apply(DatasetsRepository repository, RecordsStore recordsStore, DataExplorer dataExplorer) {
      var ds = new Datasets(repository, recordsStore, dataExplorer);
      ds.addCommand("revisions commit", CommitRevisionCommand.class);
      ds.addCommand("revisions create", CreateRevisionCommand.class);
      return ds;
   }

   @Override
   public void configure(Javalin app, ApplicationConfiguration config, RuntimeConfiguration runtime, ApplicationServices services) {
      super.configure(app, config, runtime, services);
      var handlers = DatasetsAPI.apply(getServices(runtime));

      app
         .post("/api/data/datasets/:dataset", handlers.uploadDatasetFile())
         .post("/api/data/datasets/:dataset/:revision", handlers.upload())
         .get("/api/data/datasets/:dataset/:version", handlers.downloadDatasetVersion());
   }

   @Override
   public CompletionStage<?> getDetails(DataAssetProperties properties, Object customProperties) {
      return repository.findAllVersions(properties.getId());
   }

   public DatasetServices getServices(RuntimeConfiguration runtime) {
      return DatasetServicesFactory.apply(runtime, repository, recordsStore, dataExplorer);
   }

}
