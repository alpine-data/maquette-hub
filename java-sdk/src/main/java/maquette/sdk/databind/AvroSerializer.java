package maquette.sdk.databind;

import maquette.sdk.model.records.Records;
import org.apache.avro.Schema;

public interface AvroSerializer<T> {

    Class<T> getModel();

    Schema getSchema();

    Records mapRecords(Iterable<T> value);

}
