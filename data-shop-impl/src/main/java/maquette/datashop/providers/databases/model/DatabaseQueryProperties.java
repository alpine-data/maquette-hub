package maquette.datashop.providers.databases.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.datashop.providers.databases.ports.DatabaseAnalysisResult;
import org.apache.avro.Schema;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@With
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatabaseQueryProperties {

    public static final String NAME = "name";
    public static final String RECORDS = "records";
    public static final String SCHEMA = "schema";

    @JsonProperty(NAME)
    String name;

    @JsonProperty(RECORDS)
    long records;

    @JsonProperty(SCHEMA)
    Schema schema;

    @JsonCreator
    public static DatabaseQueryProperties apply(
        @JsonProperty(NAME) String name,
        @JsonProperty(RECORDS) long records,
        @JsonProperty(SCHEMA) Schema schema
        ) {

       return new DatabaseQueryProperties(name, records, schema);
    }

}
