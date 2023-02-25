package maquette.development.values.model.governance;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

import java.util.Optional;

/**
 * Value class to store information about source code management of a model's trainng code.
 */
@With
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GitDetails {

    private final static String COMMIT = "commit";
    private final static String TRANSFER_URL = "transferUrl";
    private final static String IS_MAIN_BRANCH = "isMainBranch";

    /**
     * The Git commit id of the training code. As extracted by MLflow.
     */
    @JsonProperty("commit")
    String commit;

    /**
     * The Git remote URL of the repository during training. As extracted by MLflow.
     */
    @JsonProperty("transferUrl")
    String transferUrl;

    /**
     * Indicator whether commit was already merged into main branch when executing the training.
     */
    @JsonProperty("isMainBranch")
    boolean isMainBranch;

    public Optional<String> getCommit() {
        return Optional.of(commit);
    }

    public Optional<String> getTransferUrl() {
        return Optional.ofNullable(transferUrl);
    }

    /**
     * Creates a new instance (from JSON).
     *
     * @param commit See {@link GitDetails#commit}.
     * @param transferUrl See {@link GitDetails#transferUrl}.
     * @param isMainBranch See {@link GitDetails#isMainBranch}.
     * @return A new instance.
     */
    @JsonCreator
    public static GitDetails apply(
        @JsonProperty("commit") String commit,
        @JsonProperty("transferUrl") String transferUrl,
        @JsonProperty("isMainBranch") boolean isMainBranch
    ) {
        return new GitDetails(commit, transferUrl, isMainBranch);
    }

}
