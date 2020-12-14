package maquette.sdk.databind;

import maquette.sdk.model.records.Records;
import org.apache.avro.generic.GenericData;

import java.util.List;

public interface AvroDeserializer<T> {

    Class<T> getRecordType();

    Iterable<T> mapRecords(Records records);

    default T mapRecord(GenericData.Record record) {
        var records = Records.fromRecords(List.of(record));
        return mapRecords(records).iterator().next();
    }

}
