package maquette.core.server.commands.projects.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.DataResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class GetModelsCommand implements Command {

    String name;

    @Override
    public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
        return services
                .getProjectServices()
                .getModels(user, name)
                .thenApply(DataResult::apply);
    }

    @Override
    public Command example() {
        return apply("some-project");
    }

}
