package maquette.datashop.providers.databases.ports;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.databind.DefaultObjectMapperFactory;
import maquette.datashop.providers.databases.exceptions.QueryNotFoundException;

import java.util.List;
import java.util.Optional;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatabaseAnalysisResult {

    private static final String DATABASE = "database";

    private static final String QUERIES = "queries";


    @JsonProperty(DATABASE)
    String database;

    @JsonProperty(QUERIES)
    List<DatabaseAnalysisQueryResult> queries;

    @JsonCreator
    public static DatabaseAnalysisResult apply(
        @JsonProperty(DATABASE) String database,
        @JsonProperty(QUERIES) List<DatabaseAnalysisQueryResult> queries) {

        return new DatabaseAnalysisResult(database, queries);
    }

    public static DatabaseAnalysisResult empty(String database) {
        var columns = DefaultObjectMapperFactory
            .apply()
            .createJsonMapper(true)
            .createObjectNode();
        return DatabaseAnalysisResult.apply(database, List.of());
    }

    public DatabaseAnalysisQueryResult getQueryById(String queryId) {
        return findQueryById(queryId).orElseThrow(() -> QueryNotFoundException.applyWithId(queryId));
    }

    public Optional<DatabaseAnalysisQueryResult> findQueryById(String queryId) {
        return this
            .queries
            .stream()
            .filter(query -> query
                .getId()
                .equals(queryId))
            .findFirst();
    }

}
