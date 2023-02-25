package maquette.development.values.model.governance;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Value class to store information about code quality of a model version.
 */
@With
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CodeQuality {

    private static final String CHECKED = "checked";
    private static final String COMMIT = "commit";
    private static final String SCORE = "score";
    private static final String TEST_COVERAGE = "testCoverage";
    private static final String ISSUES = "issues";


    /**
     * Moment when check was executed.
     */
    @JsonProperty("CHECKED")
    Instant checked;

    /**
     * Commit of training code version on which check was executed.
     */
    @JsonProperty("COMMIT")
    String commit;

    /**
     * The achieved score. Should be a number in range of (0, 100).
     */
    @JsonProperty("SCORE")
    int score;

    /**
     * The test coverage of the model training code. Should be a number in range of (0, 100).
     */
    @JsonProperty("TEST_COVERAGE")
    int testCoverage;

    /**
     * A list of detected issues.
     */
    @JsonProperty("ISSUES")
    List<CodeIssue> issues;

    /**
     * Creates a new instance (from JSON).
     *
     * @param checked See {@link CodeQuality#checked}.
     * @param commit See {@link CodeQuality#commit}.
     * @param score See {@link CodeQuality#score}.
     * @param testCoverage See {@link CodeQuality#testCoverage}.
     * @param issues See {@link CodeQuality#issues}.
     * @return A new instance.
     */
    @JsonCreator
    public static CodeQuality apply(
        @JsonProperty("CHECKED") Instant checked,
        @JsonProperty("COMMIT") String commit,
        @JsonProperty("SCORE") int score,
        @JsonProperty("TEST_COVERAGE") int testCoverage,
        @JsonProperty("ISSUES") List<CodeIssue> issues
    ) {
        if (Objects.isNull(issues)) {
            issues = List.of();
        }

        return new CodeQuality(checked, commit, score, testCoverage, List.copyOf(issues));
    }

}
