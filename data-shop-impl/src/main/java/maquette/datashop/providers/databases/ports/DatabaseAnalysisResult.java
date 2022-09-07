package maquette.datashop.providers.databases.ports;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.databind.DefaultObjectMapperFactory;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatabaseAnalysisResult {

    private static final String COLUMNS = "columns";
    private static final String PROFILE = "profile";
    private static final String DATABASE = "database";
    private static final String QUERY = "query";

    @JsonProperty(COLUMNS)
    JsonNode columns;

    // TODO adapt for multiple named queries
    @JsonProperty(PROFILE)
    String profile;

    @JsonProperty(DATABASE)
    String database;

    @JsonCreator
    public static DatabaseAnalysisResult apply(
        @JsonProperty(COLUMNS) JsonNode columns,
        @JsonProperty(PROFILE) String profile,
        @JsonProperty(DATABASE) String database) {

        return new DatabaseAnalysisResult(columns, profile, database);
    }

    public static DatabaseAnalysisResult empty(String database) {
        var columns = DefaultObjectMapperFactory.apply().createJsonMapper(true).createObjectNode();
        return DatabaseAnalysisResult.apply(columns, "", database);
    }

}
