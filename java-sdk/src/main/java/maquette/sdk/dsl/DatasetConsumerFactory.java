package maquette.sdk.dsl;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.With;
import maquette.common.ObjectMapperFactory;
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

@With
@AllArgsConstructor(staticName = "apply")
public final class DatasetConsumerFactory {

    private final MaquetteConfiguration maquette;

    private final ObjectMapper om;

    private final OkHttpClient client;

    private final int batchSize;

    public static DatasetConsumerFactory apply() {
        return apply(MaquetteConfiguration.apply(), ObjectMapperFactory.apply().create(), new OkHttpClient(), 100);
    }

    public <T> Source<T, NotUsed> createSource(String dataset, String version, Class<T> recordType) {
        return createSource(dataset, version, ReflectiveAvroDeserializer.apply(recordType));
    }

    public <T> Source<T, NotUsed> createSource(String dataset, String version, AvroDeserializer<T> deserializer) {
        var request = maquette
            .createRequestFor("/api/data/datasets/%s/%s", dataset, version)
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
