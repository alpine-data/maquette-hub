package maquette.core.modules.users.model.git;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GitRepositoryProperties {

    private static final String NAME = "name";
    private static final String GIT_URL = "gitTransportUrl";
    private static final String HTTP_URL = "httpTransportUrl";

    @JsonProperty(NAME)
    String name;

    @JsonProperty(GIT_URL)
    String gitTransportUrl;

    @JsonProperty(HTTP_URL)
    String httpTransportUrl;

    @JsonCreator
    public static GitRepositoryProperties apply(@JsonProperty(NAME) String name,
                                                @JsonProperty(GIT_URL) String gitTransportUrl,
                                                @JsonProperty(HTTP_URL) String httpTransportUrl) {
        return new GitRepositoryProperties(name, gitTransportUrl, httpTransportUrl);
    }

    public static GitRepositoryProperties fake(String name) {
        return apply(name, "giturl", "httpurl");
    }

    public static GitRepositoryProperties fake() {
        return fake("fake");
    }

}
