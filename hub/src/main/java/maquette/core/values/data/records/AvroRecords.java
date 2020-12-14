package maquette.core.values.data.records;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.util.ByteBufferOutputStream;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
final class AvroRecords implements Records {

    private final List<GenericData.Record> records;

    public static AvroRecords apply(List<GenericData.Record> records) {
        Schema s = null;

        for (GenericData.Record record : records) {
            if (s != null && !s.equals(record.getSchema())) {
                throw new IllegalArgumentException("All records must have the same schema!");
            }

            s = record.getSchema();
        }

        return new AvroRecords(ImmutableList.copyOf(records));
    }

    @Override
    public Schema getSchema() {
        if (records.size() > 0) {
            return records.get(0).getSchema();
        } else {
            return SchemaBuilder.builder().nullType();
        }
    }

    @Override
    public List<GenericData.Record> getRecords() {
        return records;
    }

    @Override
    public List<ByteString> getBytes() {
        return Operators.suppressExceptions(() -> {
            ByteBufferOutputStream os = new ByteBufferOutputStream();
            writeToOutputStream(os);

            os.flush();
            return ImmutableList
                .copyOf(os
                            .getBufferList()
                            .stream()
                            .map(ByteString::fromByteBuffer)
                            .collect(Collectors.toList()));
        });
    }

    @Override
    public Source<ByteBuffer, NotUsed> getSource() {
        return Source.from(getBytes()).map(ByteString::asByteBuffer);
    }

    @Override
    public int size() {
        return records.size();
    }

    @Override
    public void toFile(Path file) {
        Operators.suppressExceptions(() -> {
            try (OutputStream os = Files.newOutputStream(file)) {
                writeToOutputStream(os);
            }
        });
    }

    private void writeToOutputStream(OutputStream os) {
        Operators.suppressExceptions(() -> {
            DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(getSchema());
            DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(datumWriter);
            dataFileWriter.create(getSchema(), os);

            for (GenericData.Record record : records) {
                dataFileWriter.append(record);
            }

            dataFileWriter.close();
        });
    }

}
