package maquette.datashop.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.MessageResult;
import maquette.core.values.user.User;
import maquette.datashop.MaquetteDataShop;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class RequestReviewDataAssetCommand implements Command {

    String name;

    String message;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime
            .getModule(MaquetteDataShop.class)
            .getServices()
            .requestReview(user, name, message)
            .thenApply(done -> MessageResult.create("Requested review for data asset."));
    }

    @Override
    public Command example() {
        return RequestReviewDataAssetCommand.apply("some-dataset", "some reason");
    }

}
