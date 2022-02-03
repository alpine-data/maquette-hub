package maquette.datashop.providers.databases.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatabaseSettings {

    private static final String DRIVER = "driver";
    private static final String CONNECTION = "connection";
    private static final String QUERY = "query";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    @JsonProperty(DRIVER)
    DatabaseDriver driver;

    @JsonProperty(CONNECTION)
    String connection;

    @JsonProperty(QUERY)
    String query;

    @JsonProperty(USERNAME)
    String username;

    @JsonProperty(PASSWORD)
    String password;

    @JsonCreator
    public static DatabaseSettings apply(
        @JsonProperty(DRIVER) DatabaseDriver driver,
        @JsonProperty(CONNECTION) String connection,
        @JsonProperty(QUERY) String query,
        @JsonProperty(USERNAME) String username,
        @JsonProperty(PASSWORD) String password) {

        return new DatabaseSettings(driver, connection, query, username, password);
    }

}
