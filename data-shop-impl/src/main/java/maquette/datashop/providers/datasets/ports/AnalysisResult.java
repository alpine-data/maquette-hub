package maquette.datashop.providers.datasets.ports;

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
public class AnalysisResult {

    private static final String COLUMNS = "columns";
    private static final String PROFILE = "profile";
    private static final String DATASET = "dataset";
    private static final String VERSION = "version";

    @JsonProperty(COLUMNS)
    JsonNode columns;

    @JsonProperty(PROFILE)
    String profile;

    @JsonProperty(DATASET)
    String dataset;

    @JsonProperty(VERSION)
    String version;

    @JsonCreator
    public static AnalysisResult apply(
        @JsonProperty(COLUMNS) JsonNode columns,
        @JsonProperty(PROFILE) String profile,
        @JsonProperty(DATASET) String dataset,
        @JsonProperty(VERSION) String version) {

        return new AnalysisResult(columns, profile, dataset, version);
    }

    public static AnalysisResult empty(String dataset, String version) {
        var columns = DefaultObjectMapperFactory
            .apply()
            .createJsonMapper(true)
            .createObjectNode();
        return AnalysisResult.apply(columns, "", dataset, version);
    }

}
