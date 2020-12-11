package maquette.sdk.dsl;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.sdk.databind.AvroDeserializer;
import maquette.sdk.databind.ReflectiveAvroDeserializer;
import maquette.sdk.model.exceptions.MaquetteRequestException;
import maquette.sdk.model.records.Records;
import okhttp3.OkHttpClient;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.DatumReader;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@AllArgsConstructor(staticName = "apply")
public final class DataSource {

   private final String name;

   private final MaquetteConfiguration config;

   private final OkHttpClient client;

   private final int batchSize;

   public static DataSource apply(String name, MaquetteConfiguration config) {
      return apply(name, config, new OkHttpClient(), 100);
   }

   public <T> Stream<T> getStream(Class<T> recordType) {
      return getStream(ReflectiveAvroDeserializer.apply(recordType));
   }

   public <T> Stream<T> getStream(AvroDeserializer<T> deserializer) {
      var request = config
         .createRequestFor("/api/data/sources/%s", name)
         .get()
         .build();

      var response = Operators.suppressExceptions(() -> client.newCall(request).execute());
      var body = response.body();


      if (response.isSuccessful() && body != null) {
         return Operators.suppressExceptions(() -> {
            final DatumReader<GenericData.Record> datumReader = new GenericDatumReader<>();
            final DataFileStream<GenericData.Record> dataFileStream = new DataFileStream<>(body.byteStream(), datumReader);

            return StreamSupport
               .stream(dataFileStream.spliterator(), false)
               .map(deserializer::mapRecord);
         });
      } else {
         throw MaquetteRequestException.apply(request, response);
      }
   }

   public <T> Source<T, NotUsed> createSource(Class<T> recordType) {
      return createSource(ReflectiveAvroDeserializer.apply(recordType));
   }

   public <T> Source<T, NotUsed> createSource(AvroDeserializer<T> deserializer) {
      var request = config
         .createRequestFor("/api/data/sources/%s", name)
         .get()
         .build();

      var response = Operators.suppressExceptions(() -> client.newCall(request).execute());
      var body = response.body();


      if (response.isSuccessful() && body != null) {
         return Operators.suppressExceptions(() -> {
            final DatumReader<GenericData.Record> datumReader = new GenericDatumReader<>();
            final DataFileStream<GenericData.Record> dataFileStream = new DataFileStream<>(body.byteStream(), datumReader);

            return Source
               .from(dataFileStream)
               .grouped(batchSize)
               .map(Records::fromRecords)
               .map(deserializer::mapRecords)
               .mapConcat(list -> list);
         });
      } else {
         throw MaquetteRequestException.apply(request, response);
      }
   }

}
