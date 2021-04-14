package maquette.asset_providers.sources;

import io.javalin.Javalin;
import io.javalin.plugin.openapi.dsl.OpenApiBuilder;
import maquette.asset_providers.sources.commands.TestDataSourceCommand;
import maquette.asset_providers.sources.model.DataSourceDetails;
import maquette.asset_providers.sources.model.DataSourceProperties;
import maquette.asset_providers.sources.ports.JdbcPort;
import maquette.asset_providers.sources.services.DataSourceServices;
import maquette.asset_providers.sources.services.DataSourceServicesFactory;
import maquette.common.DeleteOnCloseFileInputStream;
import maquette.common.Operators;
import maquette.core.config.ApplicationConfiguration;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.data.AbstractDataAssetProvider;
import maquette.core.entities.data.model.DataAssetProperties;
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
      super(TYPE_NAME, DataSourceProperties.class);
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

   @Override
   public CompletionStage<?> getDetails(DataAssetProperties properties, Object customProperties) {
      if (customProperties instanceof DataSourceProperties) {
         var props = (DataSourceProperties) customProperties;

         return entities
            .download(props)
            .exceptionally(e -> Records.empty())
            .thenApply(records -> DataSourceDetails.apply(records.size(), records.getSchema()));
      } else {
         return super.getDetails(properties, customProperties);
      }
   }

   public DataSourceServices getServices(RuntimeConfiguration runtime) {
      return DataSourceServicesFactory.create(runtime, entities);
   }

}
