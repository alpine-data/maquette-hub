package maquette.core.modules.users.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.UID;

import java.time.Instant;

@With
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserAuthenticationToken {

    private static final String ID = "id";
    private static final String SECRET = "secret";
    private static final String VALID_BEFORE = "validBefore";

    /**
     * A unique token id.
     */
    @JsonProperty(ID)
    UID id;

    /**
     * A secret string for the token.
     */
    @JsonProperty(SECRET)
    String secret;

    /**
     * A moment until the token should be accepted.
     */
    @JsonProperty(VALID_BEFORE)
    Instant validBefore;

    @JsonCreator
    public static UserAuthenticationToken apply(@JsonProperty(ID) UID id, @JsonProperty(SECRET) String secret,
                                                @JsonProperty(VALID_BEFORE) Instant validBefore) {
        return new UserAuthenticationToken(id, secret, validBefore);
    }

    public static UserAuthenticationToken fake(String secret) {
        return apply(UID.apply(), secret, Instant.now());
    }

    public static UserAuthenticationToken fake() {
        return fake("fake");
    }
}
