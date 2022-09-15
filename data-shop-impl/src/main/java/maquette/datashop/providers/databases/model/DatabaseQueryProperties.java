package maquette.datashop.providers.databases.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import org.apache.avro.Schema;

@With
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatabaseQueryProperties {

    public static final String ID = "id";
    public static final String RECORDS = "records";
    public static final String SCHEMA = "schema";

    @JsonProperty(ID)
    String id;

    @JsonProperty(RECORDS)
    long records;

    @JsonProperty(SCHEMA)
    Schema schema;

    @JsonCreator
    public static DatabaseQueryProperties apply(
        @JsonProperty(ID) String id,
        @JsonProperty(RECORDS) long records,
        @JsonProperty(SCHEMA) Schema schema
    ) {

        return new DatabaseQueryProperties(id, records, schema);
    }

}
