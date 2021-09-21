package maquette.datashop.providers.datasets.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.DataResult;
import maquette.core.values.user.User;
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.providers.datasets.Datasets;
import maquette.datashop.providers.datasets.model.DatasetVersion;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class GetVersionCommand implements Command {

    String dataset;

    String version;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime
            .getModule(MaquetteDataShop.class)
            .getProviders()
            .getByType(Datasets.class)
            .getServices()
            .getCommit(user, dataset, DatasetVersion.apply(version))
            .thenApply(DataResult::apply);
    }

    @Override
    public Command example() {
        return apply("some-dataset", "1.0.0");
    }

}
