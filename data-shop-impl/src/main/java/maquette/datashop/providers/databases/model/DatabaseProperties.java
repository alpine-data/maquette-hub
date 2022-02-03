package maquette.datashop.providers.databases.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import maquette.datashop.providers.databases.ports.DatabaseAnalysisResult;
import org.apache.avro.Schema;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@With
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatabaseProperties {

    public static final String RECORDS = "records";
    public static final String SCHEMA = "schema";
    public static final String STATISTICS = "statistics";

    @JsonProperty(RECORDS)
    long records;

    @JsonProperty(SCHEMA)
    Schema schema;

    @Nullable
    @JsonProperty(STATISTICS)
    DatabaseAnalysisResult statistics;

    @JsonCreator
    public static DatabaseProperties apply(
        @JsonProperty(RECORDS) long records,
        @JsonProperty(SCHEMA) Schema schema,
        @JsonProperty(STATISTICS) DatabaseAnalysisResult statistics) {

       return new DatabaseProperties(records, schema, statistics);
    }

    public static DatabaseProperties apply(long records, Schema schema) {
        return apply(records, schema, null);
    }

    public Optional<DatabaseAnalysisResult> getStatistics() {
        return Optional.ofNullable(statistics);
    }

}
