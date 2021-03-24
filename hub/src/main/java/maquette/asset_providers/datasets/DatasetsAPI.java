package maquette.asset_providers.datasets;

import io.javalin.http.Handler;
import io.javalin.plugin.openapi.dsl.OpenApiBuilder;
import lombok.AllArgsConstructor;
import maquette.asset_providers.datasets.model.DatasetVersion;
import maquette.asset_providers.datasets.services.DatasetServices;
import maquette.common.DeleteOnCloseFileInputStream;
import maquette.common.Operators;
import maquette.core.values.UID;
import maquette.core.values.data.records.Records;
import maquette.core.values.user.User;
import org.apache.commons.io.FileUtils;

import java.nio.file.Files;
import java.util.Objects;

@AllArgsConstructor(staticName = "apply")
public final class DatasetsAPI {

   private final DatasetServices services;

   public Handler upload() {
      var docs = OpenApiBuilder
         .document()
         .operation(op -> {
            op.summary("Upload Dataset Data");
            op.description("Uploads data to an open revision of a dataset.");
            op.addTagsItem("Dataset");
         })
         .pathParam("project", String.class, p -> p.description("The name of the project"))
         .pathParam("dataset", String.class, p -> p.description("The name of the dataset"))
         .pathParam("revision", String.class, p -> p.description("The id of the revision"))
         .json("200", String.class);

      return OpenApiBuilder.documented(docs, ctx -> {
         var user = (User) Objects.requireNonNull(ctx.attribute("user"));
         var uploaded = Objects.requireNonNull(ctx.uploadedFile("file"));
         var dataset = ctx.pathParam("dataset");
         var revision = ctx.pathParam("revision");

         var file = Files.createTempFile("maquette", "upload");
         FileUtils.copyInputStreamToFile(uploaded.getContent(), file.toFile());

         var records = Records.fromFile(file);
         var result = services
            .upload(user, dataset, UID.apply(revision), records)
            .thenApply(done -> {
               Operators.suppressExceptions(() -> Files.deleteIfExists(file));
               return "Successfully uploaded data";
            })
            .toCompletableFuture();

         ctx.result(result);
      });
   }

   public Handler uploadDatasetFile() {
      var docs = OpenApiBuilder
         .document()
         .operation(op -> {
            op.summary("Upload Dataset Data");
            op.description("Uploads data to an open revision of a dataset.");
            op.addTagsItem("Dataset");
         })
         .pathParam("project", String.class, p -> p.description("The name of the project"))
         .pathParam("dataset", String.class, p -> p.description("The name of the dataset"))
         .json("200", String.class);

      return OpenApiBuilder.documented(docs, ctx -> {
         var user = (User) Objects.requireNonNull(ctx.attribute("user"));
         var uploaded = Objects.requireNonNull(ctx.uploadedFile("file"));
         var dataset = ctx.pathParam("dataset");

         var file = Files.createTempFile("maquette", "upload");
         FileUtils.copyInputStreamToFile(uploaded.getContent(), file.toFile());

         var message = ctx.formParam("message") != null ? ctx.formParam("message") : "New version with single file upload.";

         var records = Records.fromFile(file);
         var result = services
            .create(user, dataset, records.getSchema())
            .thenCompose(revision -> services
               .upload(user, dataset, revision.getId(), records)
               .thenCompose(done -> services.commit(user, dataset, revision.getId(), message)))
            .thenApply(committedRevision -> {
               Operators.suppressExceptions(() -> Files.deleteIfExists(file));
               return committedRevision;
            })
            .toCompletableFuture();

         ctx.json(result);
      });
   }


   public Handler downloadDatasetVersion() {
      var docs = OpenApiBuilder
         .document()
         .operation(op -> {
            op.summary("Download Dataset Data");
            op.description("Downloads from a revision of a dataset.");
            op.addTagsItem("Data Assets");
         })
         .pathParam("dataset", String.class, p -> p.description("The name of the dataset"))
         .pathParam("version", String.class, p -> p.description("The version"))
         .json("200", String.class);

      return OpenApiBuilder.documented(docs, ctx -> {
         var user = (User) Objects.requireNonNull(ctx.attribute("user"));
         var dataset = ctx.pathParam("dataset");
         var version = ctx.pathParam("version");

         var result = services
            .download(user, dataset, DatasetVersion.apply(version))
            .thenApply(records -> {
               var file = Operators.suppressExceptions(() -> Files.createTempFile("mq", "download"));
               records.toFile(file);
               return DeleteOnCloseFileInputStream.apply(file);
            })
            .toCompletableFuture();

         ctx.header("Content-Disposition", "attachment; filename=" + dataset + "-" + version + ".avro");
         ctx.header("Content-Type", "application/octet-stream");
         ctx.result(result);
      });
   }

   public Handler downloadLatestDatasetVersion() {
      var docs = OpenApiBuilder
         .document()
         .operation(op -> {
            op.summary("Download Latest Dataset Version");
            op.description("Downloads the latest revision of a dataset.");
            op.addTagsItem("Data Assets");
         })
         .pathParam("dataset", String.class, p -> p.description("The name of the dataset"))
         .json("200", String.class);

      return OpenApiBuilder.documented(docs, ctx -> {
         var user = (User) Objects.requireNonNull(ctx.attribute("user"));
         var dataset = ctx.pathParam("dataset");

         var result = services
            .download(user, dataset)
            .thenApply(records -> {
               var file = Operators.suppressExceptions(() -> Files.createTempFile("mq", "download"));
               records.toFile(file);
               return DeleteOnCloseFileInputStream.apply(file);
            })
            .toCompletableFuture();

         ctx.header("Content-Disposition", "attachment; filename=" + dataset + "-latest.avro");
         ctx.header("Content-Type", "application/octet-stream");
         ctx.result(result);
      });
   }

}
