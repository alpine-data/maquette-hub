package maquette.core.modules.users.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

import java.util.List;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDetails {

    private static final String USERNAME = "username";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String ROLES = "roles";

    @With
    @JsonProperty(USERNAME)
    String username;

    @With
    @JsonProperty(NAME)
    String name;

    @With
    @JsonProperty(EMAIL)
    String email;

    @With
    @JsonProperty(ROLES)
    List<String> roles;

    @JsonCreator
    public static UserDetails apply(@JsonProperty(USERNAME) String username, @JsonProperty(NAME) String name,
                                    @JsonProperty(EMAIL) String email, @JsonProperty(ROLES) List<String> roles) {
        return new UserDetails(username, name, email, roles);
    }

    public static UserDetails fake(String username) {
        return apply(username, "name", "email", Lists.newArrayList());
    }

    public static UserDetails fake() {
        return fake("fake");
    }
}
