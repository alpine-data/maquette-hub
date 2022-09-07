package maquette.datashop.providers.databases.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatabaseQuerySettings {

    private static final String NAME = "name";

    private static final String QUERY = "query";

    @JsonProperty(NAME)
    String name;

    @JsonProperty(QUERY)
    String query;

    @JsonCreator
    public static DatabaseQuerySettings apply(@JsonProperty(NAME) String name, @JsonProperty(QUERY) String query) {
        return new DatabaseQuerySettings(name, query);
    }

}
