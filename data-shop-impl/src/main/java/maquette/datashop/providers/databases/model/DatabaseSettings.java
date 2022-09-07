package maquette.datashop.providers.databases.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.datashop.providers.databases.exceptions.AllowLocalSessionsOnlyWithCustomQueriesException;
import maquette.datashop.providers.databases.exceptions.AtLeastOneQueryException;

import java.util.List;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatabaseSettings {

    private static final String DRIVER = "driver";
    private static final String CONNECTION = "connection";
    private static final String QUERY = "query";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    private static final String ALLOW_CUSTOM_QUERIES = "allow-custom-queries";

    private static final String ALLOW_LOCAL_SESSION = "allow-local-session";

    @JsonProperty(DRIVER)
    DatabaseDriver driver;

    @JsonProperty(CONNECTION)
    String connection;

    @JsonProperty(QUERY)
    List<DatabaseQuerySettings> query;

    @JsonProperty(USERNAME)
    String username;

    @JsonProperty(PASSWORD)
    String password;

    @JsonProperty(ALLOW_CUSTOM_QUERIES)
    boolean allowCustomQueries;

    @JsonProperty(ALLOW_LOCAL_SESSION)
    boolean allowLocalSession;

    @JsonCreator
    public static DatabaseSettings apply(
        @JsonProperty(DRIVER) DatabaseDriver driver,
        @JsonProperty(CONNECTION) String connection,
        @JsonProperty(QUERY) List<DatabaseQuerySettings> query,
        @JsonProperty(USERNAME) String username,
        @JsonProperty(PASSWORD) String password,
        @JsonProperty(ALLOW_CUSTOM_QUERIES) boolean allowCustomQueries,
        @JsonProperty(ALLOW_LOCAL_SESSION) boolean allowLocalSession) {

        if (allowLocalSession && !allowCustomQueries) {
            throw AllowLocalSessionsOnlyWithCustomQueriesException.apply();
        }

        if (query.isEmpty()) {
            throw AtLeastOneQueryException.apply();
        }

        // TODO Add further validation (queries)

        return new DatabaseSettings(driver, connection, List.copyOf(query), username, password, allowCustomQueries, allowLocalSession);
    }

}
