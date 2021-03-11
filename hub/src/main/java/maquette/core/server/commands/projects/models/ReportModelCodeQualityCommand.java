package maquette.core.server.commands.projects.models;

import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.projects.model.model.governance.CodeIssue;
import maquette.core.entities.projects.model.model.governance.IssueType;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.MessageResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;

import java.util.List;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class ReportModelCodeQualityCommand implements Command {

   String project;

   String model;

   String version;

   String commit;

   int score;

   int coverage;

   List<CodeIssue> issues;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getProjectServices()
         .reportCodeQuality(user, project, model, version, commit, score, coverage, issues)
         .thenApply(pid -> MessageResult.apply("Successfully submitted code quality results"));
   }

   @Override
   public Command example() {
      return apply(
         "some-project",
         "some-model",
         "1",
         "abcdefg",
         8,
         43,
         Lists.newArrayList(
            CodeIssue.apply("foo.py:23", IssueType.CRITICAL, "This line contains Python and Python sucks."),
            CodeIssue.apply("foo.py:12", IssueType.WARNING, "Could you please give a type hint!?")
         ));
   }

}
