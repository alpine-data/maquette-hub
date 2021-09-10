package maquette.core.modules.users.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

@With
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GitSettings {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String PRIVATE_KEY = "privateKey";
    private static final String PUBLIC_KEY = "publicKey";

    @JsonProperty(USERNAME)
    String username;

    @JsonProperty(PASSWORD)
    String password;

    @JsonProperty(PRIVATE_KEY)
    String privateKey;

    @JsonProperty(PUBLIC_KEY)
    String publicKey;

    @JsonCreator
    public static GitSettings apply(@JsonProperty(USERNAME) String username, @JsonProperty(PASSWORD) String password,
                                        @JsonProperty(PRIVATE_KEY) String privateKey,
                                        @JsonProperty(PUBLIC_KEY) String publicKey) {
        return new GitSettings(username, password, privateKey, publicKey);
    }

    public boolean isEmpty() {
        return (username == null || username.trim().isEmpty())
                && (password == null || password.trim().isEmpty())
                && (privateKey == null || privateKey.trim().isEmpty())
                && (publicKey == null || publicKey.trim().isEmpty());
    }

}
