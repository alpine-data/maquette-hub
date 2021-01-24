package maquette.sdk.dsl;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import lombok.AllArgsConstructor;
import maquette.sdk.databind.AvroDeserializer;
import maquette.sdk.databind.ReflectiveAvroDeserializer;
import maquette.sdk.model.records.Records;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.DatumReader;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@AllArgsConstructor(staticName = "apply")
public final class DataSource {

   private static final int BATCH_SIZE = 100;

   private final String name;

   private final MaquetteClient client;

   /**
    * Reads data from a data source.
    *
    * @param recordType The expected output type.
    * @param <T>        The expected output type.
    * @return A data stream.
    */
   public <T> Stream<T> read(Class<T> recordType) {
      return read(ReflectiveAvroDeserializer.apply(recordType));
   }

   /**
    * Reads data from a data source.
    *
    * @param deserializer The deserializer to de-serialize the Avro record to the expected output type.
    * @param <T>          The expected output type.
    * @return A data stream.
    */
   public <T> Stream<T> read(AvroDeserializer<T> deserializer) {
      var request = client
         .createRequestFor("/api/data/sources/%s", name)
         .get()
         .build();

      return client.executeRequest(request, res -> {
         final DatumReader<GenericData.Record> datumReader = new GenericDatumReader<>();
         final DataFileStream<GenericData.Record> dataFileStream = new DataFileStream<>(res.byteStream(), datumReader);

         return StreamSupport
            .stream(dataFileStream.spliterator(), false)
            .map(deserializer::mapRecord);
      });
   }

   /**
    * Creates an Akka Streams source to read data from a data source.
    *
    * @param recordType The expected type of the record.
    * @param <T>        Rhe output type.
    * @return A new Akka Streams source.
    */
   public <T> Source<T, NotUsed> source(Class<T> recordType) {
      return source(ReflectiveAvroDeserializer.apply(recordType));
   }

   /**
    * Creates an Akka Streams source to read data from a data source.
    *
    * @param deserializer The serializer to parse the Avro record.
    * @param <T>          The expected output type.
    * @return A new Akka Streams source.
    */
   public <T> Source<T, NotUsed> source(AvroDeserializer<T> deserializer) {
      var request = client
         .createRequestFor("/api/data/sources/%s", name)
         .get()
         .build();

      return client.executeRequest(request, body -> {
         final DatumReader<GenericData.Record> datumReader = new GenericDatumReader<>();
         final DataFileStream<GenericData.Record> dataFileStream = new DataFileStream<>(body.byteStream(), datumReader);

         return Source
            .from(dataFileStream)
            .grouped(BATCH_SIZE)
            .map(Records::fromRecords)
            .map(deserializer::mapRecords)
            .mapConcat(list -> list);
      });
   }

}
