package maquette.datashop.providers.databases.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.datashop.providers.DataAssetSettings;
import maquette.datashop.providers.databases.exceptions.AllowLocalSessionsOnlyWithCustomQueriesException;
import maquette.datashop.providers.databases.exceptions.AtLeastOneQueryException;
import maquette.datashop.providers.databases.exceptions.QueryNotFoundException;

import java.util.List;
import java.util.Optional;

@With
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatabaseSettings implements DataAssetSettings {

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

        return new DatabaseSettings(driver, connection, List.copyOf(query), username, password, allowCustomQueries, allowLocalSession);
    }

    public DatabaseQuerySettings getQueryById(String queryId) {
        return findQueryById(queryId).orElseThrow(() -> QueryNotFoundException.applyWithId(queryId));
    }

    public Optional<DatabaseQuerySettings> findQueryById(String queryId) {
        return this
            .query
            .stream()
            .filter(query -> query.getId().equals(queryId))
            .findFirst();
    }

    public DatabaseQuerySettings getQueryByName(String queryName) {
        return findQueryByName(queryName).orElseThrow(() -> QueryNotFoundException.applyWithName(queryName));
    }

    public Optional<DatabaseQuerySettings> findQueryByName(String queryName) {
        return this
            .query
            .stream()
            .filter(query -> query.getName().equals(queryName))
            .findFirst();
    }

    @Override
    public DataAssetSettings getObfuscated() {
        return this.withPassword(this.password);
    }
}
