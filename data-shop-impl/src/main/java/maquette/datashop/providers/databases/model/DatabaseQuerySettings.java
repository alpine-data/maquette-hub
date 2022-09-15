package maquette.datashop.providers.databases.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.common.Operators;
import maquette.datashop.providers.databases.exceptions.DatabaseQueryMayNotBeEmptyException;
import maquette.datashop.providers.databases.exceptions.QueryNameMayNotBeEmptyException;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatabaseQuerySettings {

    private static final String NAME = "name";

    private static final String QUERY = "query";

    private static final String ID = "id";

    @JsonProperty(NAME)
    String name;

    @JsonProperty(QUERY)
    String query;

    @JsonProperty(ID)
    String id;

    @JsonCreator
    public static DatabaseQuerySettings apply(@JsonProperty(NAME) String name, @JsonProperty(QUERY) String query,
                                              @JsonProperty(ID) String id) {
        if (name == null || name
            .strip()
            .length() == 0) {
            throw QueryNameMayNotBeEmptyException.apply();
        } else if (query == null || query
            .strip()
            .length() == 0) {
            throw DatabaseQueryMayNotBeEmptyException.apply(name);
        } else if (id == null || id
            .strip()
            .length() == 0) {
            throw new IllegalArgumentException("`id` must not be null.");
        }

        return new DatabaseQuerySettings(name, query, id);
    }

    public static DatabaseQuerySettings apply(String name, String query) {
        return apply(name, query, Operators.randomHash());
    }

}
