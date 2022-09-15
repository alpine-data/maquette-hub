package maquette.development.commands.models;

import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.MessageResult;
import maquette.core.values.UID;
import maquette.core.values.user.User;
import maquette.development.MaquetteModelDevelopment;
import maquette.development.values.model.governance.CodeIssue;
import maquette.development.values.model.governance.IssueType;

import java.util.List;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class ReportModelQualityCommand implements Command {

    String workspace;

    String model;

    String version;

    String commit;

    int score;

    int coverage;

    List<CodeIssue> issues;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime
            .getModule(MaquetteModelDevelopment.class)
            .getWorkspaceServices()
            .reportCodeQuality(user, workspace, model, version, commit, score, coverage, issues)
            .thenApply(pid -> MessageResult.apply("Successfully submitted code quality results."));
    }

    @Override
    public Command example() {
        return apply("some-workspace", "some-model",
            "1.0",
            UID
                .apply()
                .getValue(),
            8,
            43,
            Lists.newArrayList(
                CodeIssue.apply("foo.py:23", IssueType.CRITICAL, "This line contains Python and Python sucks."),
                CodeIssue.apply("foo.py:12", IssueType.WARNING, "Could you please give a type hint!?")
            ));
    }

}
