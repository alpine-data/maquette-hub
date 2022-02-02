package maquette.development.values.stacks;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class PostgresStackConfiguration implements StackConfiguration {

    private static final String NAME = "name";
    private static final String DB_USERNAME = "dbUsername";
    private static final String DB_PASSWORD = "dbPassword";
    private static final String PG_EMAIL = "pgEmail";
    private static final String PG_PASSWORD = "pgPassword";
    private static final String ENVIRONMENT = "environment";

    @JsonProperty(NAME)
    private final String name;

    @JsonProperty(DB_USERNAME)
    private final String dbUsername;

    @JsonProperty(DB_PASSWORD)
    private final String dbPassword;

    @JsonProperty(PG_EMAIL)
    private final String pgEmail;

    @JsonProperty(PG_PASSWORD)
    private final String pgPassword;

    /**
     * Environment variables which should be set in the stacks nodes.
     */
    @JsonProperty(ENVIRONMENT)
    Map<String, String> environmentVariables;

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

        return new PostgresStackConfiguration(name, dbUsername, dbPassword, pgEmail, pgPassword, environmentVariables);
    }

    @Override
    public Map<String, String> getEnvironmentVariables() {
        return Map.copyOf(environmentVariables);
    }

    @Override
    public String getStackInstanceName() {
        return name;
    }

    @Override
    public List<String> getResourceGroups() {
        return List.of();
    }

    @Override
    public StackConfiguration withStackInstanceName(String name) {
        return PostgresStackConfiguration.apply(name, dbUsername, dbPassword, pgEmail, pgPassword, environmentVariables);
    }

    @Override
    public StackConfiguration withEnvironmentVariables(Map<String, String> environment) {
        return PostgresStackConfiguration.apply(name, dbUsername, dbPassword, pgEmail, pgPassword, environment);
    }

}
