package maquette.core.server.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.MessageResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CreateProjectCommand implements Command {

    String name;

    String summary;

    @Override
    public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
        if (Objects.isNull(name) || name.length() == 0) {
            return CompletableFuture.failedFuture(new RuntimeException("`name` must be supplied"));
        }

        return services
                .getProjectServices()
                .create(user, name)
                .thenApply(pid -> MessageResult.apply("Successfully created project"));
    }

    @Override
    public Command example() {
        return CreateProjectCommand.apply("my-funny-project", "lorem ipsum");
    }

}
