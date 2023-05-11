package maquette.development.values.stacks;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Value
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostgresStackConfiguration extends DefaultStackConfiguration {

    private static final String DB_USERNAME = "dbUsername";
    private static final String DB_PASSWORD = "dbPassword";
    private static final String PG_EMAIL = "pgEmail";
    private static final String PG_PASSWORD = "pgPassword";

    @JsonProperty(DB_USERNAME)
    String dbUsername;

    @JsonProperty(DB_PASSWORD)
    String dbPassword;

    @JsonProperty(PG_EMAIL)
    String pgEmail;

    @JsonProperty(PG_PASSWORD)
    String pgPassword;

    @JsonCreator
    public static PostgresStackConfiguration apply(
        @JsonProperty(NAME) String name,
        @JsonProperty(DB_USERNAME) String dbUsername,
        @JsonProperty(DB_PASSWORD) String dbPassword,
        @JsonProperty(PG_EMAIL) String pgEmail,
        @JsonProperty(PG_PASSWORD) String pgPassword,
        @JsonProperty(ENVIRONMENT) Map<String, String> environmentVariables,
        @JsonProperty(USER_EMAIL) String userEmail,
        @JsonProperty(SECURED) Boolean secured) {

        if (Objects.isNull(environmentVariables)) {
            environmentVariables = Maps.newHashMap();
        }

        var instance = new PostgresStackConfiguration(dbUsername, dbPassword, pgEmail, pgPassword);
        instance.name = name;
        instance.environmentVariables = environmentVariables;
        instance.userEmail = userEmail;
        instance.secured = secured == null || Boolean.TRUE.equals(secured);

        return instance;
    }

    @Override
    public List<String> getResourceGroups() {
        return List.of();
    }

}
