package maquette.datashop.providers.databases.ports;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatabaseAnalysisQueryResult {

    private static final String ID = "id";

    private static final String COLUMNS = "columns";

    private static final String PROFILE = "profile";

    @JsonProperty(ID)
    String id;

    @JsonProperty(COLUMNS)
    JsonNode columns;

    @JsonProperty(PROFILE)
    String profile;


    @JsonCreator
    public static DatabaseAnalysisQueryResult apply(
        @JsonProperty(ID) String id,
        @JsonProperty(COLUMNS) JsonNode columns,
        @JsonProperty(PROFILE) String profile
    ) {

        return new DatabaseAnalysisQueryResult(id, columns, profile);
    }

}
