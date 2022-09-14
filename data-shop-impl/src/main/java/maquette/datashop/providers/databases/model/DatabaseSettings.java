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

    private static final String SESSION_SETTINGS = "session-settings";

    private static final String QUERY_SETTINGS = "query-setting";

    private static final String ALLOW_CUSTOM_QUERIES = "allow-custom-queries";

    private static final String ALLOW_LOCAL_SESSION = "allow-local-session";

    @JsonProperty(SESSION_SETTINGS)
    DatabaseSessionSettings sessionSettings;

    @JsonProperty(QUERY_SETTINGS)
    List<DatabaseQuerySettings> querySettings;

    @JsonProperty(ALLOW_CUSTOM_QUERIES)
    boolean allowCustomQueries;

    @JsonProperty(ALLOW_LOCAL_SESSION)
    boolean allowLocalSession;

    @JsonCreator
    public static DatabaseSettings apply(
        @JsonProperty(SESSION_SETTINGS) DatabaseSessionSettings sessionSettings,
        @JsonProperty(QUERY_SETTINGS) List<DatabaseQuerySettings> querySettings,
        @JsonProperty(ALLOW_CUSTOM_QUERIES) boolean allowCustomQueries,
        @JsonProperty(ALLOW_LOCAL_SESSION) boolean allowLocalSession) {

        if (allowLocalSession && !allowCustomQueries) {
            throw AllowLocalSessionsOnlyWithCustomQueriesException.apply();
        }

        if (querySettings.isEmpty()) {
            throw AtLeastOneQueryException.apply();
        }

        return new DatabaseSettings(sessionSettings, List.copyOf(querySettings), allowCustomQueries, allowLocalSession);
    }

    public DatabaseQuerySettings getQueryById(String queryId) {
        return findQueryById(queryId).orElseThrow(() -> QueryNotFoundException.applyWithId(queryId));
    }

    public Optional<DatabaseQuerySettings> findQueryById(String queryId) {
        return this
            .querySettings
            .stream()
            .filter(query -> query.getId().equals(queryId))
            .findFirst();
    }

    public DatabaseQuerySettings getQueryByName(String queryName) {
        return findQueryByName(queryName).orElseThrow(() -> QueryNotFoundException.applyWithName(queryName));
    }

    public Optional<DatabaseQuerySettings> findQueryByName(String queryName) {
        return this
            .querySettings
            .stream()
            .filter(query -> query.getName().equals(queryName))
            .findFirst();
    }

    @Override
    public DataAssetSettings getObfuscated() {
        return this.withSessionSettings(sessionSettings.withPassword("***"));
    }
}
