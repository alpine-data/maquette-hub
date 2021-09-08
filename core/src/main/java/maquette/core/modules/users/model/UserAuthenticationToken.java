package maquette.core.modules.users.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.UID;

import java.time.Instant;

@Value
@AllArgsConstructor(staticName = "apply")
public class UserAuthenticationToken {

    /**
     * A unique token id.
     */
    UID id;

    /**
     * A secret string for the token.
     */
    String secret;

    /**
     * A moment until the token should be accepted.
     */
    Instant validBefore;

}
