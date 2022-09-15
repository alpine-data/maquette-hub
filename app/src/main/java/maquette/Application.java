package maquette;

import maquette.adapters.MaquetteDataAssetsServiceAdapter;
import maquette.adapters.MaquetteWorkspacesServiceAdapter;
import maquette.core.Maquette;
import maquette.core.MaquetteRuntime;
import maquette.core.ports.email.FakeEmailClient;
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.ports.InMemoryDataAssetsRepository;
import maquette.datashop.providers.FakeProvider;
import maquette.development.MaquetteModelDevelopment;
import maquette.development.ports.InMemoryModelsRepository;
import maquette.development.ports.InMemorySandboxesRepository;
import maquette.development.ports.InMemoryWorkspacesRepository;
import maquette.development.ports.infrastructure.FakeInfrastructurePort;

/**
 * This object ensembles Maquette Community Edition.
 */
public class Application {

    public static void main(String[] args) {
        var maquette = Maquette
            .apply()
            .configure(Application::configure)
            .start();

        Runtime
            .getRuntime()
            .addShutdownHook(new Thread(maquette::stop));
    }

    private static MaquetteRuntime configure(MaquetteRuntime runtime) {
        var om = runtime
            .getObjectMapperFactory()
            .createJsonMapper();
        var dataAssetsAdapter = MaquetteDataAssetsServiceAdapter.apply(om);
        var workspacesAdapter = MaquetteWorkspacesServiceAdapter.apply(om);

        var dataAssetsRepository = InMemoryDataAssetsRepository.apply();

        var workspacesRepository = InMemoryWorkspacesRepository.apply();
        var modelsRepository = InMemoryModelsRepository.apply();
        var sandboxesRepository = InMemorySandboxesRepository.apply();
        var infrastructurePort = FakeInfrastructurePort.apply();
        var modelDevelopment = MaquetteModelDevelopment.apply(
            runtime, workspacesRepository, modelsRepository, sandboxesRepository, infrastructurePort,
            dataAssetsAdapter);

        var shop = MaquetteDataShop
            .apply(dataAssetsRepository, workspacesAdapter, FakeEmailClient.apply(), FakeProvider.apply());

        dataAssetsAdapter.setMaquetteModule(shop);
        workspacesAdapter.setMaquetteModule(modelDevelopment);

        return runtime
            .withModule(rt -> shop)
            .withModule(rt -> modelDevelopment);
    }

}
