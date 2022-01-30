package maquette.development.values.stacks;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.development.values.EnvironmentType;

import java.util.List;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class PostgresStackConfiguration implements StackConfiguration {

    private static final String NAME = "name";
    private static final String DB_USERNAME = "dbUsername";
    private static final String DB_PASSWORD = "dbPassword";
    private static final String PG_EMAIL = "pgEmail";
    private static final String PG_PASSWORD = "pgPassword";

    @JsonProperty(NAME)
    private final String name;

    @JsonProperty(DB_USERNAME)
    private final String dbUsername;

    @JsonProperty(DB_PASSWORD)
    private final String dbPassword;

    @JsonProperty(PG_EMAIL)
    private final String pgEmail;

    @JsonProperty
    private final String pgPassword;

    @JsonCreator
    public static PostgresStackConfiguration apply(
        @JsonProperty(NAME) String name,
        @JsonProperty(DB_USERNAME) String dbUsername,
        @JsonProperty(DB_PASSWORD) String dbPassword,
        @JsonProperty(PG_EMAIL) String pgEmail,
        @JsonProperty(PG_PASSWORD) String pgPassword) {

        return new PostgresStackConfiguration(name, dbUsername, dbPassword, pgEmail, pgPassword);
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
    public StackInstanceParameters getInstanceParameters(Map<String, String> parameters, EnvironmentType environment) {
        return StackInstanceParameters.apply("http://foo.bar", "Open Notebook");
    }

    @Override
    public StackConfiguration withStackInstanceName(String name) {
        return PostgresStackConfiguration.apply(name, dbUsername, dbPassword, pgEmail, pgPassword);
    }

}
