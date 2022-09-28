package maquette.datashop.providers.databases.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.datashop.providers.databases.ports.DatabaseAnalysisResult;

import java.util.List;
import java.util.Optional;

@With
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatabaseProperties {

    private static final String QUERY_PROPERTIES = "queryProperties";

    private static final String QUERY_STATISTICS = "queryStatistics";

    @JsonProperty(QUERY_PROPERTIES)
    public List<DatabaseQueryProperties> queryProperties;

    @JsonProperty(QUERY_STATISTICS)
    public DatabaseAnalysisResult queryStatistics;

    @JsonCreator
    public static DatabaseProperties apply(
        @JsonProperty(QUERY_PROPERTIES) List<DatabaseQueryProperties> queryProperties,
        @JsonProperty(QUERY_STATISTICS) DatabaseAnalysisResult queryStatistics) {
        return new DatabaseProperties(queryProperties, queryStatistics);
    }

    public Optional<DatabaseAnalysisResult> getQueryStatistics() {
        return Optional.ofNullable(queryStatistics);
    }

}
