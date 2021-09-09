package maquette.core.modules.users.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

import java.util.Optional;

@With
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSettings {

    private static final String GIT = "git";

    @JsonProperty(GIT)
    GitSettings git;

    @JsonCreator
    public static UserSettings apply(@JsonProperty(GIT) GitSettings git) {
        return new UserSettings(git);
    }

    public static UserSettings fake(String name) {
        return apply(GitSettings.apply(name, "password", "privkey", "pubkey"));
    }

    public static UserSettings fake() {
        return fake("fake");
    }

    public Optional<GitSettings> getGit() {
        return Optional.ofNullable(git);
    }
}
