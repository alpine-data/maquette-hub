package maquette.sdk.dsl;

import akka.Done;
import akka.NotUsed;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import com.google.common.collect.Lists;
import com.jayway.jsonpath.JsonPath;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.sdk.commands.CommitRevisionCommand;
import maquette.sdk.commands.CreateRevisionCommand;
import maquette.sdk.databind.AvroDeserializer;
import maquette.sdk.databind.AvroSerializer;
import maquette.sdk.databind.ReflectiveAvroDeserializer;
import maquette.sdk.databind.ReflectiveAvroSerializer;
import maquette.sdk.model.records.Records;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.DatumReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * DSL which contains functions to read and write from Datasets.
 */
@AllArgsConstructor(staticName = "apply")
public final class Dataset {

   private static final int BATCH_SIZE = 100; // TODO: configure batch size

   private final String name;

   private final MaquetteClient client;

   /**
    * Creates a source to read a Dataset.
    *
    * @param recordType A Java type which is used to parse records from the Dataset.
    * @param <T>        The output type of the source.
    * @return A new Akka Strams source.
    */
   public <T> Source<T, NotUsed> source(Class<T> recordType) {
      return source(recordType, null);
   }

   /**
    * Creates a source to read a Dataset.
    *
    * @param recordType A Java type which is used to parse records from the Dataset.
    * @param version    The version to read from.
    * @param <T>        The output type of the source.
    * @return A new Akka Strams source.
    */
   public <T> Source<T, NotUsed> source(Class<T> recordType, String version) {
      return source(ReflectiveAvroDeserializer.apply(recordType), version);
   }

   /**
    * Creates a source to read latest version of a Dataset.
    *
    * @param deserializer The deserializer used to read the Avro records from the Dataset.
    * @param <T>          The output type of the source.
    * @return A new Akka Streams source.
    */
   public <T> Source<T, NotUsed> source(AvroDeserializer<T> deserializer) {
      return source(deserializer, null);
   }

   /**
    * Creates a source to read a Dataset.
    *
    * @param deserializer The deserializer used to read the Avro records from the Dataset.
    * @param version      The version to read from.
    * @param <T>          The output type of the source.
    * @return A new Akka Streams source.
    */
   public <T> Source<T, NotUsed> source(AvroDeserializer<T> deserializer, String version) {
      var dataFileStream = downloadDatasetVersion(version);
      return Source
         .from(dataFileStream)
         .grouped(BATCH_SIZE)
         .map(Records::fromRecords)
         .map(deserializer::mapRecords)
         .mapConcat(list -> list);
   }

   /**
    * Creates a flow to upload new version to a dataset.
    *
    * @param recordType The java type which is the input to the flow.
    * @param message    The message to describe the new version.
    * @param <T>        The input/ output data type of the flow.
    * @return An Akka Streams flow.
    */
   public <T> Flow<T, T, CompletionStage<Done>> flow(Class<T> recordType, String message) {
      return flow(
         ReflectiveAvroSerializer.apply(recordType),
         message);
   }

   /**
    * Creates a flow to upload new version to a dataset.
    *
    * @param serializer The serializer which s used to to transform the input data to Avro records.
    * @param message    The message to describe the new version.
    * @param <T>        The input/ output data type of the flow.
    * @return An Akka Streams flow.
    */
   public <T> Flow<T, T, CompletionStage<Done>> flow(AvroSerializer<T> serializer, String message) {
      var created = new AtomicReference<CreatedRevision<T>>(null);

      return Flow
         .of(serializer.getModel())
         .grouped(BATCH_SIZE)
         .map(DatasetRequest::apply)
         .prepend(Source
            .single(1)
            .map(i -> createRevision(name, serializer)))
         .map(request -> {
            if (request instanceof CreatedRevision) {
               created.set((CreatedRevision<T>) request);
               return Lists.<T>newArrayList();
            } else if (request instanceof PushRecordsRequest) {
               var revisionId = created.get().getRevisionId();
               var records = ((PushRecordsRequest<T>) request).records;
               pushRecords(name, records, revisionId, serializer);
               return records;
            } else {
               return Lists.<T>newArrayList();
            }
         })
         .mapConcat(list -> list)
         .watchTermination(Keep.right())
         .mapMaterializedValue(done -> done.thenApply(d -> {
            publishDatasetVersion(name, created.get().revisionId, message);
            return Done.getInstance();
         }));
   }

   /**
    * Creates an Akka Streams sink to upload a new version to dataset.
    *
    * @param recordType The java type which is the input to the sink.
    * @param message    The message to describe the new version.
    * @param <T>        The input data type of the sink.
    * @return The Akka Stream sink.
    */
   public <T> Sink<T, CompletionStage<Done>> sink(Class<T> recordType, String message) {
      return sink(ReflectiveAvroSerializer.apply(recordType), message);
   }

   /**
    * Creates an Akka Streams sink to upload a new version to a dataset.
    *
    * @param serializer The serializer which s used to to transform the input data to Avro records.
    * @param message    The message to describe the new version.
    * @param <T>        The input data type of the sink.
    * @return The Akka Stream sink.
    */
   public <T> Sink<T, CompletionStage<Done>> sink(AvroSerializer<T> serializer, String message) {
      return Flow
         .of(serializer.getModel())
         .viaMat(flow(serializer, message), Keep.right())
         .toMat(Sink.ignore(), Keep.left());
   }

   /**
    * Read data from a dataset.
    *
    * @param recordType   The expected output type.
    * @param <T>          The output type.
    * @return A stream of data.
    */
   public <T> Stream<T> read(Class<T> recordType) {
      return read(recordType, null);
   }

   /**
    * Read data from a dataset.
    *
    * @param recordType   The expected output type.
    * @param version      The version to fetch from the dataset.
    * @param <T>          The output type.
    * @return A stream of data.
    */
   public <T> Stream<T> read(Class<T> recordType, String version) {
      return read(ReflectiveAvroDeserializer.apply(recordType), version);
   }

   /**
    * Read data from a dataset.
    *
    * @param deserializer The serializer used to de-serialize Avro records.
    * @param <T>          The output type.
    * @return A stream of data.
    */
   public <T> Stream<T> read(AvroDeserializer<T> deserializer) {
      return read(deserializer, null);
   }

   /**
    * Read data from a dataset.
    *
    * @param deserializer The serializer used to de-serialize Avro records.
    * @param version      The version to fetch from the dataset.
    * @param <T>          The output type.
    * @return A stream of data.
    */
   public <T> Stream<T> read(AvroDeserializer<T> deserializer, String version) {
      var records = StreamSupport
         .stream(downloadDatasetVersion(version).spliterator(), false)
         .collect(Collectors.toList());

      return StreamSupport.stream(deserializer
         .mapRecords(Records.fromRecords(List.copyOf(records)))
         .spliterator(), false);
   }

   /**
    * Creates a new version in the dataset.
    *
    * @param collection The data to be uploaded to the dataset.
    * @param message    The message to describe the new version.
    * @param <T>        The type of the data.
    * @return Done.
    */
   @SuppressWarnings("unchecked")
   public <T> CompletionStage<Done> write(java.util.Collection<T> collection, String message) {
      if (collection.isEmpty()) {
         return CompletableFuture.completedFuture(Done.getInstance());
      }

      var type = (Class<T>) collection.iterator().next().getClass();
      return write(collection, message, type);
   }

   /**
    * Creates a new version in the dataset.
    *
    * @param collection The data to be uploaded to the dataset.
    * @param message    The message to describe the new version.
    * @param recordType The type of the data.
    * @param <T>        The type of teh data.
    * @return Done.
    */
   public <T> CompletionStage<Done> write(java.util.Collection<T> collection, String message, Class<T> recordType) {
      return write(collection, message, ReflectiveAvroSerializer.apply(recordType));
   }

   /**
    * Creates a new version in the dataset.
    *
    * @param collection The data to be uploaded to the dataset.
    * @param message    The message to describe the new version.
    * @param serializer The serializer to be used to transform the data to an Avro Record.
    * @param <T>        The input type of the data.
    * @return Done.
    */
   public <T> CompletionStage<Done> write(java.util.Collection<T> collection, String message, AvroSerializer<T> serializer) {
      var created = createRevision(name, serializer);
      pushRecords(name, List.copyOf(collection), created.getRevisionId(), serializer);
      publishDatasetVersion(name, created.getRevisionId(), message);

      return CompletableFuture.completedFuture(Done.getInstance());
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

      Request request = client
         .createRequestFor("/api/data/datasets/%s/%s", dataset, revisionId)
         .post(requestBody)
         .build();

      client.executeRequest(request);
   }

   private <T> CreatedRevision<T> createRevision(String dataset, AvroSerializer<T> serializer) {
      var command = CreateRevisionCommand.apply(dataset, serializer.getSchema());
      return client.executeCommand(
         command,
         res -> CreatedRevision.apply(JsonPath.read(res, "$.data.id")));
   }

   private void publishDatasetVersion(String dataset, String revision, String message) {
      var command = CommitRevisionCommand.apply(dataset, revision, message);
      client.executeCommand(command);
   }

   private DataFileStream<GenericData.Record> downloadDatasetVersion(String version) {
      Request.Builder requestBuilder;

      if (version == null) {
         requestBuilder = client
            .createRequestFor("/api/data/datasets/%s", name);
      } else {
         requestBuilder = client
            .createRequestFor("/api/data/datasets/%s/%s", name, version);
      }

      var request = requestBuilder
         .get()
         .build();

      return client.executeRequest(request, res -> {
         final DatumReader<GenericData.Record> datumReader = new GenericDatumReader<>();
         return new DataFileStream<>(res.byteStream(), datumReader);
      });
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
