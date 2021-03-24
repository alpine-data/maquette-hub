package maquette.asset_providers.collections;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.dsl.OpenApiBuilder;
import lombok.AllArgsConstructor;
import maquette.asset_providers.collections.services.CollectionServices;
import maquette.common.DeleteOnCloseFileInputStream;
import maquette.common.Operators;
import maquette.core.values.data.binary.BinaryObject;
import maquette.core.values.data.binary.BinaryObjects;
import maquette.core.values.user.User;
import org.apache.commons.io.FileUtils;

import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor()
public final class CollectionsAPI {

   private final CollectionServices services;

   public Handler upload() {
      var docs = OpenApiBuilder
         .document()
         .operation(op -> {
            op.summary("Upload a file to a collection.");
            op.addTagsItem("Collections");
         })
         .pathParam("collection", String.class, p -> p.description("The name of the collection"))
         .json("200", String.class);

      return OpenApiBuilder.documented(docs, ctx -> {
         var user = (User) Objects.requireNonNull(ctx.attribute("user"));
         var uploaded = Objects.requireNonNull(ctx.uploadedFile("file"));
         var collection = ctx.pathParam("collection");
         var message = ctx.formParam("message", "");
         var name = ctx.formParam("name", uploaded.getFilename());
         var basePath = ctx.formParam("basePath");

         var file = Files.createTempFile("maquette", "upload");
         FileUtils.copyInputStreamToFile(uploaded.getContent(), file.toFile());

         var bin = BinaryObjects.fromFile(file);

         CompletableFuture<String> result;

         if (Objects.isNull(basePath)) {
            result = services
               .put(user, collection, bin, name, message)
               .thenApply(done -> {
                  Operators.suppressExceptions(() -> Files.deleteIfExists(file));
                  return "Successfully uploaded data";
               })
               .toCompletableFuture();
         } else {
            result = services
               .putAll(user, collection, bin, basePath, message)
               .thenApply(done -> {
                  Operators.suppressExceptions(() -> Files.deleteIfExists(file));
                  return "Successfully uploaded files";
               })
               .toCompletableFuture();
         }

         ctx.result(result);
      });
   }

   public Handler download() {
      var docs = OpenApiBuilder
         .document()
         .operation(op -> {
            op.summary("Downloads the whole collection as a zip file");
            op.addTagsItem("Collections");
         })
         .pathParam("collection", String.class, p -> p.description("The name of the collection"))
         .json("200", String.class);

      return OpenApiBuilder.documented(docs, ctx -> {
         var user = (User) Objects.requireNonNull(ctx.attribute("user"));
         var collection = ctx.pathParam("collection");

         CompletionStage<BinaryObject> download;

         if (ctx.pathParamMap().containsKey("tag")) {
            var tag = ctx.pathParam("tag");

            download = services
               .readAll(user, collection, tag);
         } else {
            download = services
               .readAll(user, collection);
         }

         var result = download
            .thenCompose(bin -> {
               var tmp = Operators.suppressExceptions(() -> Files.createTempFile("mq", "download"));

               ctx.header("Content-Disposition", "attachment; filename=" + collection + ".zip");
               ctx.header("Content-Type", "application/octet-stream");

               return bin
                  .toFile(tmp)
                  .thenCompose(d -> bin.discard())
                  .thenApply(done -> DeleteOnCloseFileInputStream.apply(tmp));
            })
            .toCompletableFuture();

         ctx.result(result);
      });
   }

   public Handler downloadFile() {
      var docs = OpenApiBuilder
         .document()
         .operation(op -> {
            op.summary("Download a file from a collection");
            op.addTagsItem("Collections");
         })
         .pathParam("collection", String.class, p -> p.description("The name of the collection"))
         .pathParam("file", String.class, p -> p.description("The name of the file"))
         .json("200", String.class);

      return OpenApiBuilder.documented(docs, ctx -> {
         var user = (User) Objects.requireNonNull(ctx.attribute("user"));
         var collection = ctx.pathParam("collection");
         var file = getRemainingPath(ctx);

         CompletionStage<BinaryObject> download;

         if (ctx.pathParamMap().containsKey("tag")) {
            var tag = ctx.pathParam("tag");

            download = services.read(user, collection, tag, file);
         } else {
            download = services.read(user, collection, file);
         }

         var result = download
            .thenCompose(bin -> {
               var tmp = Operators.suppressExceptions(() -> Files.createTempFile("mq", "download"));
               var filename = Arrays.stream(file.split("/")).reduce((f, s) -> s).orElse(collection);

               ctx.header("Content-Disposition", "attachment; filename=" + filename);
               ctx.header("Content-Type", "application/octet-stream");

               return bin
                  .toFile(tmp)
                  .thenCompose(d -> bin.discard())
                  .thenApply(done -> DeleteOnCloseFileInputStream.apply(tmp));
            })
            .toCompletableFuture();

         ctx.result(result);
      });
   }

   public Handler remove() {
      var docs = OpenApiBuilder
         .document()
         .operation(op -> {
            op.summary("Remove a file from a collection");
            op.addTagsItem("Collections");
         })
         .pathParam("collection", String.class, p -> p.description("The name of the collection"))
         .pathParam("file", String.class, p -> p.description("The name of the file"))
         .json("200", String.class);

      return OpenApiBuilder.documented(docs, ctx -> {
         var user = (User) Objects.requireNonNull(ctx.attribute("user"));
         var collection = ctx.pathParam("collection");
         var file = getRemainingPath(ctx);

         var result = services
            .remove(user, collection, file)
            .thenApply(done -> "Successfully deleted file")
            .toCompletableFuture();

         ctx.result(result);
      });
   }

   private String getRemainingPath(Context ctx) {
      var matchedPathElements = Arrays
         .stream(ctx.endpointHandlerPath().split("/"))
         .filter(e -> !e.equals("*"))
         .filter(e -> e.length() > 0)
         .collect(Collectors.toList());

      var allPathElements = Arrays
         .stream(ctx.path().split("/"))
         .filter(e -> e.length() > 0)
         .collect(Collectors.toList());

      return String.join("/", allPathElements
         .subList(matchedPathElements.size(), allPathElements.size()));
   }

}
