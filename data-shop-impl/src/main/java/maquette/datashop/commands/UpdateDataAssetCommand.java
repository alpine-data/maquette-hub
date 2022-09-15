package maquette.datashop.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.common.Operators;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.MessageResult;
import maquette.core.values.user.User;
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.values.metadata.*;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public final class UpdateDataAssetCommand implements Command {

    String name;

    DataAssetMetadata metadata;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime
            .getModule(MaquetteDataShop.class)
            .getServices()
            .update(user, name, metadata)
            .thenApply(done -> MessageResult.create("Successfully updated data asset."));
    }

    @Override
    public Command example() {
        var meta = DataAssetMetadata.apply(
            "some-dataset", "title", Operators.lorem(),
            DataVisibility.PUBLIC, DataClassification.PUBLIC, PersonalInformation.NONE, DataZone.RAW, null);

        return apply("some-dataset", meta);
    }

}
