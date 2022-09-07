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

        // TODO Add further validation (queries)

        return new DatabaseSettings(sessionSettings, List.copyOf(querySettings), allowCustomQueries, allowLocalSession);
    }

}
