package maquette.datashop.commands.requests;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.common.Operators;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.MessageResult;
import maquette.core.values.UID;
import maquette.core.values.user.User;
import maquette.datashop.MaquetteDataShop;

import java.time.Instant;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class GrantAccessRequestCommand implements Command {

    String name;

    UID id;

    Instant until;

    String message;

    String environment;

    boolean downstreamApprovalRequired;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime
            .getModule(MaquetteDataShop.class)
            .getServices()
            .grantDataAccessRequest(user, name, id, until, message, environment, downstreamApprovalRequired, runtime)
            .thenApply(done -> MessageResult.create("Data Access Request has been granted successfully"));
    }

    @Override
    public Command example() {
        return apply("some-dataset", UID.apply(), Instant.now(), Operators.lorem(), "any", true);
    }
}
