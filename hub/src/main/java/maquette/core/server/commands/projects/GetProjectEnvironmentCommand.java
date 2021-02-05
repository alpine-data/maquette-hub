package maquette.core.server.commands.projects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.DataResult;
import maquette.core.services.ApplicationServices;
import maquette.core.services.projects.EnvironmentType;
import maquette.core.values.user.User;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class GetProjectEnvironmentCommand implements Command {

    String name;

    EnvironmentType type;

    @Override
    public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
        var environmentType = type != null ? type : EnvironmentType.EXTERNAL;

        return services
                .getProjectServices()
                .environment(user, name, environmentType)
                .thenApply(DataResult::apply);
    }

    @Override
    public Command example() {
        return apply("some-project", EnvironmentType.EXTERNAL);
    }

}
