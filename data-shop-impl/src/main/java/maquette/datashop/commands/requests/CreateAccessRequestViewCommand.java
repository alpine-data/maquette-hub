package maquette.datashop.commands.requests;

import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.values.user.User;
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.commands.views.CreateAccessRequestView;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class CreateAccessRequestViewCommand implements Command {

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime
            .getModule(MaquetteDataShop.class)
            .getServices()
            .getUsersWorkspaces(user)
            .thenApply(CreateAccessRequestView::apply);
    }

    @Override
    public Command example() {
        return CreateAccessRequestViewCommand.apply();
    }

}
