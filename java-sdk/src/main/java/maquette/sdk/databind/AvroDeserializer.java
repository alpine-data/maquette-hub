package maquette.sdk.databind;

import maquette.sdk.model.records.Records;

public interface AvroDeserializer<T> {

    Class<T> getRecordType();

    Iterable<T> mapRecords(Records records);

}
