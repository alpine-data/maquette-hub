package maquette.datashop.specs.steps;

import com.google.common.collect.Lists;
import maquette.core.MaquetteRuntime;
import maquette.core.values.user.AuthenticatedUser;
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.ports.FakeWorkspacesServicePort;
import maquette.datashop.providers.databases.Databases;
import maquette.datashop.providers.datasets.Datasets;
import maquette.datashop.providers.datasets.commands.ListVersionsCommand;
import maquette.datashop.providers.datasets.model.DatasetVersion;
import maquette.datashop.providers.datasets.records.Records;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

public class DatabaseStepDefinitions extends DataAssetStepDefinitions {


    public DatabaseStepDefinitions(MaquetteRuntime runtime, FakeWorkspacesServicePort workspaces) {
        super(runtime, workspaces, Lists.newArrayList(), Lists.newArrayList(), Lists.newArrayList(), null);
    }
}
