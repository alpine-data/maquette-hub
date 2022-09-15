package maquette.development.commands.models;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.MessageResult;
import maquette.core.values.user.User;
import maquette.development.MaquetteModelDevelopment;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class AnswerModelQuestionnaireCommand implements Command {

    String workspace;

    String model;

    String version;

    JsonNode answers;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime
            .getModule(MaquetteModelDevelopment.class)
            .getWorkspaceServices()
            .answerQuestionnaire(user, workspace, model, version, answers)
            .thenApply(pid -> MessageResult.apply("Successfully submitted questionnaire answers."));
    }

    @Override
    public Command example() {
        return apply("some-workspace", "model", "version", null);
    }

}
