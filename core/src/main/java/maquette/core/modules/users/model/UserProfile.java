package maquette.core.modules.users.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.UID;
import org.apache.commons.codec.digest.DigestUtils;

@With
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserProfile {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String TITLE = "title";
    private static final String BIO = "bio";
    private static final String EMAIL = "email";
    private static final String PHONE = "phone";
    private static final String LOCATION = "location";
    private static final String AVATAR = "avatar";

    @JsonProperty(ID)
    UID id;

    @JsonProperty(NAME)
    String name;

    @JsonProperty(TITLE)
    String title;

    @JsonProperty(BIO)
    String bio;

    @JsonProperty(EMAIL)
    String email;

    @JsonProperty(PHONE)
    String phone;

    @JsonProperty(LOCATION)
    String location;

    @JsonProperty(AVATAR)
    String avatar;

    @JsonCreator
    public static UserProfile apply(@JsonProperty(ID) UID id, @JsonProperty(NAME) String name,
                                    @JsonProperty(TITLE) String title, @JsonProperty(BIO) String bio,
                                    @JsonProperty(EMAIL) String email, @JsonProperty(PHONE) String phone,
                                    @JsonProperty(LOCATION) String location) {
        return new UserProfile(id, name, title, bio, email, phone, location, null);
    }

    public static UserProfile fake(String name) {
        return apply(UID.apply(), name, "title", "bio", "email", "phone", "location");
    }

    public static UserProfile fake() {
        return fake("fake");
    }

    @JsonProperty(AVATAR)
    public String getAvatar() {
        return "https://www.gravatar.com/avatar/" + DigestUtils.md5Hex(email != null ? email : "foo@bar.de") + "?d" +
            "=retro";
    }

}
