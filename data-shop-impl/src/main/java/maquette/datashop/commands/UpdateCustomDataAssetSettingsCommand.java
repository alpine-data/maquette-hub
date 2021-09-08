package maquette.datashop.commands;

import com.fasterxml.jackson.databind.JsonNode;
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
import maquette.datashop.values.DataAsset;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public final class UpdateCustomDataAssetSettingsCommand implements Command {

    String name;

    JsonNode customSettings;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        var shop = runtime.getModule(MaquetteDataShop.class);

        return shop
            .getServices()
            .get(user, name)
            .thenApply(DataAsset::getProperties)
            .thenApply(properties -> shop.getProviders().getByName(properties.getType()))
            .thenCompose(assetProvider -> {
                var customSettings = Operators.suppressExceptions(() ->
                    runtime.getObjectMapperFactory()
                        .createJsonMapper()
                        .treeToValue(this.customSettings, assetProvider.getSettingsType()));

                return shop
                    .getServices()
                    .updateCustomSettings(user, name, customSettings)
                    .thenApply(done -> MessageResult.apply("Successfully updated data asset."));
            });
    }

    @Override
    public Command example() {
        return apply("some-dataset", null);
    }

}
