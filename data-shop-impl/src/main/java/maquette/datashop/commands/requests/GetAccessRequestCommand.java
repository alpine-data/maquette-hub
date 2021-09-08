package maquette.datashop.commands.requests;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.DataResult;
import maquette.core.values.UID;
import maquette.core.values.user.User;
import maquette.datashop.MaquetteDataShop;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class GetAccessRequestCommand implements Command {

    String name;

    UID id;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime
            .getModule(MaquetteDataShop.class)
            .getServices()
            .getDataAccessRequest(user, name, id)
            .thenApply(DataResult::apply);
    }

    @Override
    public Command example() {
        return apply("some-dataset", UID.apply("1"));
    }

}
