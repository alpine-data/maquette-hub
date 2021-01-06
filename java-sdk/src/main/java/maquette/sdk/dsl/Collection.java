package maquette.sdk.dsl;

import akka.Done;
import akka.NotUsed;
import akka.japi.Function;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.sdk.commands.CreateCollectionTagCommand;
import maquette.sdk.model.exceptions.MaquetteRequestException;
import okhttp3.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class Collection {

   private final String name;

   private final MaquetteConfiguration maquette;

   private final OkHttpClient client;

   public static Collection apply(String name, MaquetteConfiguration maquette) {
      return apply(name, maquette, new OkHttpClient());
   }

   public Flow<Path, Path, NotUsed> createPathFlow(String message) {
      return createFlow(Path.class, p -> FileUpload.apply(p.getFileName().toString(), p), message);
   }

   public Flow<Path, Path, NotUsed> createPathFlow(String message, Path basePath) {
      return createFlow(Path.class, p -> FileUpload.apply(basePath.relativize(p).toString(), p), message);
   }

   public <T> Flow<T, T, NotUsed> createFlow(Class<T> type, Function<T, FileUpload> mapToFile, String message) {
      return Flow
         .of(type)
         .map(record -> {
            var file = mapToFile.apply(record);

            if (Files.isRegularFile(file.file)) {
               upload(file.file, file.name, message);
            }

            return record;
         });
   }

   public Sink<Path, CompletionStage<Done>> createPathSink(String message) {
      return Flow
         .of(Path.class)
         .via(createPathFlow(message))
         .toMat(Sink.ignore(), Keep.right());
   }

   public Sink<Path, CompletionStage<Done>> createPathSink(String message, Path basePath) {
      return Flow
         .of(Path.class)
         .via(createPathFlow(message, basePath))
         .toMat(Sink.ignore(), Keep.right());
   }

   public <T> Sink<T, CompletionStage<Done>> createSink(Class<T> type, Function<T, FileUpload> mapToFile, String message) {
      return Flow
         .of(type)
         .via(createFlow(type, mapToFile, message))
         .toMat(Sink.ignore(), Keep.right());
   }

   public CompletionStage<Done> tag(String tag, String message) {
      return maquette.executeCommand(CreateCollectionTagCommand.apply(name, tag, message));
   }

   public CompletionStage<Done> upload(Path file, String name, String message) {
      RequestBody requestBody = new MultipartBody.Builder()
         .setType(MultipartBody.FORM)
         .addFormDataPart(
            "file", "file",
            RequestBody.create(file.toFile(), MediaType.parse("application/octet-stream")))
         .addFormDataPart("message", message)
         .addFormDataPart("name", name)
         .build();

      Request request = maquette
         .createRequestFor("/api/data/collections/%s", this.name)
         .post(requestBody)
         .build();

      Response response = Operators.suppressExceptions(() -> client.newCall(request).execute());

      if (!response.isSuccessful()) {
         throw MaquetteRequestException.apply(request, response);
      }

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Value
   @AllArgsConstructor(staticName = "apply")
   public static class FileUpload {

      String name;

      Path file;

   }

}
