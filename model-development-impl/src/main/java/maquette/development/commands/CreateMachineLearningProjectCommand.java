package maquette.development.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.common.Operators;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.MessageResult;
import maquette.core.values.user.User;
import maquette.development.MaquetteModelDevelopment;
import maquette.development.values.mlproject.MLProjectType;

import java.util.concurrent.CompletionStage;

/**
 * This command is used to request the creation of a new Machine Learning project.
 * <p>
 * See
 * {@link maquette.development.services.WorkspaceServices#createMachineLearningProject(User, String, String, MLProjectType)}.
 */
@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CreateMachineLearningProjectCommand implements Command {

    /**
     * See
     * {@link maquette.development.services.WorkspaceServices#createMachineLearningProject(User, String, String, MLProjectType)}.
     */
    String workspace;

    /**
     * See
     * {@link maquette.development.services.WorkspaceServices#createMachineLearningProject(User, String, String, MLProjectType)}.
     */
    String projectName;

    /**
     * See
     * {@link maquette.development.services.WorkspaceServices#createMachineLearningProject(User, String, String, MLProjectType)}.
     */
    MLProjectType templateType;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime
            .getModule(MaquetteModelDevelopment.class)
            .getWorkspaceServices()
            .createMachineLearningProject(user, workspace, projectName, templateType)
            .thenApply(done -> MessageResult.apply("Successfully created workspace", done));
    }

    @Override
    public Command example() {
        return apply("some-workspace", "some-project", MLProjectType.DEFAULT);
    }

}
