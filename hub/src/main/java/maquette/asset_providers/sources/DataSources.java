package maquette.asset_providers.sources;

import akka.Done;
import io.javalin.Javalin;
import io.javalin.plugin.openapi.dsl.OpenApiBuilder;
import maquette.asset_providers.sources.commands.TestDataSourceCommand;
import maquette.asset_providers.sources.model.DataSourceProperties;
import maquette.asset_providers.sources.model.DataSourceSettings;
import maquette.asset_providers.sources.ports.JdbcPort;
import maquette.asset_providers.sources.services.DataSourceServices;
import maquette.asset_providers.sources.services.DataSourceServicesFactory;
import maquette.common.DeleteOnCloseFileInputStream;
import maquette.common.Operators;
import maquette.core.config.ApplicationConfiguration;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.data.AbstractDataAssetProvider;
import maquette.core.entities.data.DataAssetEntity;
import maquette.core.services.ApplicationServices;
import maquette.core.values.data.records.Records;
import maquette.core.values.user.User;

import java.nio.file.Files;
import java.util.Objects;
import java.util.concurrent.CompletionStage;

public final class DataSources extends AbstractDataAssetProvider {

   public static final String TYPE_NAME = "source";

   private final DataSourceEntities entities;

   private DataSources(JdbcPort jdbcPort) {
      super(TYPE_NAME, DataSourceSettings.class, DataSourceProperties.class, DataSourceProperties.apply(0, Records.empty().getSchema()));
      this.entities = DataSourceEntities.apply(jdbcPort);
   }

   public static DataSources apply(JdbcPort jdbcPort) {
      var ds = new DataSources(jdbcPort);
      ds.addCommand("test", TestDataSourceCommand.class);
      return ds;
   }

   @Override
   public void configure(Javalin app, ApplicationConfiguration config, RuntimeConfiguration runtime, ApplicationServices services) {
      super.configure(app, config, runtime, services);
      var service = getServices(runtime);

      var docs = OpenApiBuilder
         .document()
         .operation(op -> {
            op.summary("Download Datasource");
            op.description("Downloads data from a data source.");
            op.addTagsItem("Data Assets");
         })
         .pathParam("source", String.class, p -> p.description("The name of the data source"))
         .json("200", String.class);

      app.get("/api/data/sources/:source", OpenApiBuilder.documented(docs, ctx -> {
         var user = (User) Objects.requireNonNull(ctx.attribute("user"));
         var source = ctx.pathParam("source");

         var result = service
            .download(user, source)
            .thenApply(records -> {
               var file = Operators.suppressExceptions(() -> Files.createTempFile("mq", "download"));
               records.toFile(file);
               return DeleteOnCloseFileInputStream.apply(file);
            })
            .toCompletableFuture();

         ctx.header("Content-Disposition", "attachment; filename=" + source + ".avro");
         ctx.header("Content-Type", "application/octet-stream");
         ctx.result(result);
      }));
   }

   public DataSourceServices getServices(RuntimeConfiguration runtime) {
      return DataSourceServicesFactory.create(runtime, entities);
   }

   @Override
   public CompletionStage<Done> onCreated(DataAssetEntity entity, Object customSettings) {
      if (customSettings instanceof DataSourceSettings) {
         var settings = (DataSourceSettings) customSettings;
         return entities
            .download(settings)
            .exceptionally(e -> Records.empty())
            .thenApply(records -> DataSourceProperties.apply(records.size(), records.getSchema()))
            .thenCompose(entity::updateCustomProperties);
      } else {
         return super.onCreated(entity, customSettings);
      }
   }

   @Override
   public CompletionStage<Done> onUpdatedCustomSettings(DataAssetEntity entity) {
      return entity
         .getCustomSettings(DataSourceSettings.class)
         .thenCompose(settings -> onCreated(entity, settings));
   }

}
