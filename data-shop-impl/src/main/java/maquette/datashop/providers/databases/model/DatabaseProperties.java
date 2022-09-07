package maquette.datashop.providers.databases.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import maquette.datashop.providers.databases.ports.DatabaseAnalysisResult;

import java.util.List;

@With
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatabaseProperties {

    private static final String QUERY_PROPERTIES = "query-properties";

    private static final String QUERY_STATISTICS = "query-statistics";

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

}
