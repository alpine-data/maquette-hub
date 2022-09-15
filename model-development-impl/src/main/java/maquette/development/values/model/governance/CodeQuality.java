package maquette.development.values.model.governance;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

import java.time.Instant;
import java.util.List;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class CodeQuality {

    Instant checked;

    String commit;

    int score;

    int testCoverage;

    List<CodeIssue> issues;

}
