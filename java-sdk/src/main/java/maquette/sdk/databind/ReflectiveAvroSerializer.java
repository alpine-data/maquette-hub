package maquette.sdk.databind;

import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.sdk.model.records.Records;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.avro.util.ByteBufferOutputStream;

@AllArgsConstructor(staticName = "apply")
public final class ReflectiveAvroSerializer<T> implements AvroSerializer<T> {

    private final Class<T> model;

    private final Schema schema;

    public static <T> ReflectiveAvroSerializer<T> apply(Class<T> model) {
        Schema schema = ReflectData.get().getSchema(model);
        return apply(model, schema);
    }

    @Override
    public Class<T> getModel() {
        return model;
    }

    @Override
    public Schema getSchema() {
        return schema;
    }

    @Override
    public Records mapRecords(Iterable<T> values) {
        return Operators.suppressExceptions(() -> {
            ReflectDatumWriter<T> datumWriter = new ReflectDatumWriter<>(schema);
            ByteBufferOutputStream os = new ByteBufferOutputStream();
            DataFileWriter<T> dataFileWriter = new DataFileWriter<>(datumWriter);

            dataFileWriter.create(schema, os);

            for (T value : values) {
                dataFileWriter.append(value);
            }

            dataFileWriter.flush();
            dataFileWriter.close();

            return Records.fromByteBuffers(os.getBufferList());
        });
    }

}
