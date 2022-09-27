package maquette.development.values.stacks;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@With
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class PostgresStackConfiguration extends DefaultStackConfiguration {

    private static final String DB_USERNAME = "dbUsername";
    private static final String DB_PASSWORD = "dbPassword";
    private static final String PG_EMAIL = "pgEmail";
    private static final String PG_PASSWORD = "pgPassword";

    @JsonProperty(DB_USERNAME)
    private final String dbUsername;

    @JsonProperty(DB_PASSWORD)
    private final String dbPassword;

    @JsonProperty(PG_EMAIL)
    private final String pgEmail;

    @JsonProperty(PG_PASSWORD)
    private final String pgPassword;

    @JsonCreator
    public static PostgresStackConfiguration apply(
        @JsonProperty(NAME) String name,
        @JsonProperty(DB_USERNAME) String dbUsername,
        @JsonProperty(DB_PASSWORD) String dbPassword,
        @JsonProperty(PG_EMAIL) String pgEmail,
        @JsonProperty(PG_PASSWORD) String pgPassword,
        @JsonProperty(ENVIRONMENT) Map<String, String> environmentVariables) {

        if (Objects.isNull(environmentVariables)) {
            environmentVariables = Maps.newHashMap();
        }

        var instance = new PostgresStackConfiguration(dbUsername, dbPassword, pgEmail, pgPassword);
        instance.name = name;
        instance.environmentVariables = environmentVariables;
        return instance;
    }

    @Override
    public List<String> getResourceGroups() {
        return List.of();
    }

}
