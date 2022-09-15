package maquette.datashop.providers.collections.commands;

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
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.providers.collections.Collections;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CreateCollectionTagCommand implements Command {

    String collection;

    String tag;

    String message;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime
            .getModule(MaquetteDataShop.class)
            .getProviders()
            .getByType(Collections.class)
            .getServices()
            .tag(user, collection, tag, message)
            .thenApply(pid -> MessageResult.create("Successfully created tag `%s` on collection.", tag, collection));
    }

    @Override
    public Command example() {
        return apply("some-collection", "tag", Operators.lorem());
    }

}
