package maquette.core.modules.users.model.git;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.UID;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDetails {

    private static final String ID = "id";
    private static final String EMAIL = "email";
    private static final String NAME = "name";

    @JsonProperty(ID)
    UID id;

    @JsonProperty(EMAIL)
    String email;

    @JsonProperty(NAME)
    String name;

    @JsonCreator
    public static UserDetails apply(
        @JsonProperty(ID) UID id,
        @JsonProperty(EMAIL) String email,
        @JsonProperty(NAME) String name) {

        return new UserDetails(id, email, name);
    }

    public static UserDetails fake(String name) {
        return apply(UID.apply(), "email", name);
    }

    public static UserDetails fake() {
        return fake("fake");
    }

}
