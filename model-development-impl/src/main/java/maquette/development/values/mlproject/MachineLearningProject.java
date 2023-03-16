package maquette.development.values.mlproject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * This value class represents information of a machine learning project.
 * A machine learning project is some source code managed within a Git
 * repository which can be used to train models.
 * <p>
 * Mars does not directly manage these projects, but we store information
 * about them to link them from within related workspaces.
 * <p>
 * A machine learning project is usually linked with one workspace.
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MachineLearningProject {

    private static final String NAME = "name";
    private static final String URL = "url";
    private static final String GIT_URL = "gitUrl";

    private static final String CATALOG_URL = "catalog_url";

    /**
     * The name of the ML project's repository.
     */
    @JsonProperty(NAME)
    String name;

    /**
     * The URL of the Git repository "homepage" - Where one can read the repos README, huma accessible.
     */
    @JsonProperty(URL)
    String url;

    /**
     * The URL to check out the source code, either HTTPS or SSL.
     */
    @JsonProperty(GIT_URL)
    String gitUrl;

    /**
     * The URL of the companies component catalog (e.g. Backstage).
     */
    @JsonProperty(CATALOG_URL)
    String catalogUrl;

    @JsonCreator
    public static MachineLearningProject apply(
        @JsonProperty(NAME) String name,
        @JsonProperty(URL) String url,
        @JsonProperty(GIT_URL) String gitUrl,
        @JsonProperty(CATALOG_URL) String catalogUrl
    ) {
        return new MachineLearningProject(name, url, gitUrl, catalogUrl);
    }

}
