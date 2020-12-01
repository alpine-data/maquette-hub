package maquette.sdk.dsl;

import akka.Done;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.jayway.jsonpath.JsonPath;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.common.ObjectMapperFactory;
import maquette.common.Operators;
import maquette.sdk.commands.CommitRevisionCommand;
import maquette.sdk.commands.CreateRevisionCommand;
import maquette.sdk.databind.AvroSerializer;
import maquette.sdk.databind.ReflectiveAvroSerializer;
import maquette.sdk.model.exceptions.MaquetteRequestException;
import okhttp3.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicReference;

@With
@AllArgsConstructor(staticName = "apply")
public final class DatasetProducerFactory {

   private final MaquetteConfiguration maquette;

   private final ObjectMapper om;

   private final OkHttpClient client;

   private final int batchSize;

   public static DatasetProducerFactory apply() {
      return apply(MaquetteConfiguration.apply(), ObjectMapperFactory.apply().create(), new OkHttpClient(), 100);
   }

   public <T> Flow<T, T, CompletionStage<Done>> createFlow(String dataset, Class<T> recordType) {
      return createFlow(dataset, recordType, "Published new data from source application.");
   }


   public <T> Flow<T, T, CompletionStage<Done>> createFlow(String dataset, Class<T> recordType, String message) {
      return createFlow(
         dataset,
         ReflectiveAvroSerializer.apply(recordType),
         message);
   }

   public <T> Flow<T, T, CompletionStage<Done>> createFlow(String dataset, AvroSerializer<T> serializer) {
      return createFlow(dataset, serializer, "Published new data from source application.");
   }

   public <T> Flow<T, T, CompletionStage<Done>> createFlow(String dataset, AvroSerializer<T> serializer, String message) {
      var created = new AtomicReference<CreatedRevision<T>>(null);

      return Flow
         .of(serializer.getModel())
         .grouped(batchSize)
         .map(DatasetRequest::apply)
         .prepend(Source
            .single(1)
            .map(i -> createRevision(dataset, serializer)))
         .map(request -> {
            if (request instanceof CreatedRevision) {
               created.set((CreatedRevision<T>) request);
               return Lists.<T>newArrayList();
            } else if (request instanceof PushRecordsRequest) {
               var revisionId = created.get().getRevisionId();
               var records = ((PushRecordsRequest<T>) request).records;
               pushRecords(dataset, records, revisionId, serializer);
               return records;
            } else {
               return Lists.<T>newArrayList();
            }
         })
         .mapConcat(list -> list)
         .watchTermination(Keep.right())
         .mapMaterializedValue(done -> done.thenApply(d -> {
            publishDatasetVersion(dataset, created.get().revisionId, message);
            return Done.getInstance();
         }));
   }

   public <T> Sink<T, CompletionStage<Done>> createSink(String dataset, Class<T> recordType) {
      return createSink(
         dataset,
         ReflectiveAvroSerializer.apply(recordType));
   }

   public <T> Sink<T, CompletionStage<Done>> createSink(String dataset, AvroSerializer<T> serializer) {
      return Flow
         .of(serializer.getModel())
         .viaMat(createFlow(dataset, serializer), Keep.right())
         .toMat(Sink.ignore(), Keep.left());
   }

   private <T> void pushRecords(
      String dataset, List<T> records, String revisionId, AvroSerializer<T> serializer) {

      Path tmp = Operators.suppressExceptions(() -> Files.createTempFile("mq", "records"));
      serializer.mapRecords(records).toFile(tmp);

      RequestBody requestBody = new MultipartBody.Builder()
         .setType(MultipartBody.FORM)
         .addFormDataPart(
            "file", "file",
            RequestBody.create(tmp.toFile(), MediaType.parse("avro/binary")))
         .build();

      Request request = maquette
         .createRequestFor("/api/data/datasets/%s/%s", dataset, revisionId)
         .post(requestBody)
         .build();

      Response response = Operators.suppressExceptions(() -> client.newCall(request).execute());

      if (!response.isSuccessful()) {
         throw MaquetteRequestException.apply(request, response);
      }
   }

   private <T> DatasetRequest<T> createRevision(String dataset, AvroSerializer<T> serializer) {
      var command = CreateRevisionCommand.apply(dataset, serializer.getSchema());
      var json = Operators.suppressExceptions(() -> om.writeValueAsString(command));

      var request = maquette
         .createRequestFor("/api/commands")
         .post(RequestBody.create(json, MediaType.parse("application/json; charset=utf-8")))
         .build();

      var response = Operators.suppressExceptions(() -> client.newCall(request).execute());

      return Operators.suppressExceptions(() -> {
         ResponseBody body = response.body();

         if (response.isSuccessful() && body != null) {
            var jsonResponse = body.string();
            String id = JsonPath.read(jsonResponse, "$.data.id");
            return CreatedRevision.apply(id);
         } else {
            throw MaquetteRequestException.apply(request, response);
         }
      });
   }

   private void publishDatasetVersion(String dataset, String revision, String message) {
      var command = CommitRevisionCommand.apply(dataset, revision, message);
      var json = Operators.suppressExceptions(() -> om.writeValueAsString(command));

      var request =  maquette
         .createRequestFor("/api/commands", dataset, revision)
         .post(RequestBody.create(json, MediaType.parse("application/json; charset=utf-8")))
         .build();

      var response = Operators.suppressExceptions(() -> client.newCall(request).execute());

      if (!response.isSuccessful()) {
         throw MaquetteRequestException.apply(request, response);
      }
   }

   @SuppressWarnings("unused")
   private interface DatasetRequest<T> {

      static <T> DatasetRequest<T> apply(List<T> records) {
         return PushRecordsRequest.apply(records);
      }

   }

   @Value
   @AllArgsConstructor(staticName = "apply")
   private static class CreatedRevision<T> implements DatasetRequest<T> {

      String revisionId;

   }

   @Value
   @AllArgsConstructor(staticName = "apply")
   private static class PushRecordsRequest<T> implements DatasetRequest<T> {

      List<T> records;

   }

}
