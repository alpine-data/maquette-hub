package maquette.datashop.providers.databases.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

@With
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatabaseSessionSettings {

    private static final String DRIVER = "driver";

    private static final String CONNECTION = "connection";

    private static final String USERNAME = "username";

    private static final String PASSWORD = "password";

    @JsonProperty(DRIVER)
    DatabaseDriver driver;

    @JsonProperty(CONNECTION)
    String connection;

    @JsonProperty(USERNAME)
    String username;

    @JsonProperty(PASSWORD)
    String password;

    @JsonCreator
    public static DatabaseSessionSettings apply(
        @JsonProperty(DRIVER) DatabaseDriver driver,
        @JsonProperty(CONNECTION) String connection,
        @JsonProperty(USERNAME) String username,
        @JsonProperty(PASSWORD) String password) {

        return new DatabaseSessionSettings(driver, connection, username, password);
    }

}
