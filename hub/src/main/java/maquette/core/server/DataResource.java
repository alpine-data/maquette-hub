package maquette.core.server;

import io.javalin.http.Handler;
import io.javalin.plugin.openapi.dsl.OpenApiBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.datasets.model.DatasetVersion;
import maquette.core.entities.datasets.model.records.Records;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@AllArgsConstructor()
public final class DataResource {

   private final ApplicationServices services;

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
         var project = ctx.pathParam("project");
         var dataset = ctx.pathParam("dataset");
         var revision = ctx.pathParam("revision");

         var file = Files.createTempFile("maquette", "upload");
         FileUtils.copyInputStreamToFile(uploaded.getContent(), file.toFile());

         var records = Records.fromFile(file);
         var result = services
            .getDatasetServices()
            .upload(user, project, dataset, revision, records)
            .thenApply(done -> {
               Operators.suppressExceptions(() -> Files.deleteIfExists(file));
               return "Successfully uploaded data";
            })
            .toCompletableFuture();

         ctx.result(result);
      });
   }

   public Handler download() {
      var docs = OpenApiBuilder
         .document()
         .operation(op -> {
            op.summary("Download Dataset Data");
            op.description("Downloads from a revision of a dataset.");
            op.addTagsItem("Dataset");
         })
         .pathParam("project", String.class, p -> p.description("The name of the project"))
         .pathParam("dataset", String.class, p -> p.description("The name of the dataset"))
         .pathParam("revision", String.class, p -> p.description("The id of the revision"))
         .json("200", String.class);

      return OpenApiBuilder.documented(docs, ctx -> {
         var user = (User) Objects.requireNonNull(ctx.attribute("user"));
         var project = ctx.pathParam("project");
         var dataset = ctx.pathParam("dataset");
         var version = ctx.pathParam("version");

         var result = services
            .getDatasetServices()
            .download(user, project, dataset, DatasetVersion.apply(version))
            .thenApply(records -> {
               var file = Operators.suppressExceptions(() -> Files.createTempFile("mq", "download"));
               records.toFile(file);
               return DeleteOnCloseInputStream.apply(file);
            })
            .toCompletableFuture();

         ctx.header("Content-Disposition", "attachment; filename=" + dataset + "-" + version + ".avro");
         ctx.header("Content-Type", "application/octet-stream");
         ctx.result(result);
      });
   }

   @AllArgsConstructor(access = AccessLevel.PRIVATE)
   private static class DeleteOnCloseInputStream extends InputStream {

      private final Path file;

      private final InputStream delegate;

      public static DeleteOnCloseInputStream apply(Path file) {
         return new DeleteOnCloseInputStream(file, Operators.suppressExceptions(() -> Files.newInputStream(file)));
      }

      @Override
      public int read() throws IOException {
         return delegate.read();
      }

      @Override
      public void close() throws IOException {
         delegate.close();
         Files.deleteIfExists(file);
      }
   }

}
