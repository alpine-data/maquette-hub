package maquette.core.server.commands.projects;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.MessageResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class AnswerModelQuestionnaireCommand implements Command {

   String project;

   String model;

   String version;

   JsonNode answers;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getProjectServices()
         .answerQuestionnaire(user, project, model, version, answers)
         .thenApply(pid -> MessageResult.apply("Successfully submitted questionnaire answers."));
   }

   @Override
   public Command example() {
      return apply("some-project", "Some Project", "1", null);
   }

}
