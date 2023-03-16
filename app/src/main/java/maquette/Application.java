package maquette;

import maquette.adapters.MaquetteDataAssetsServiceAdapter;
import maquette.adapters.MaquetteModelDevelopmentAdapter;
import maquette.adapters.MaquetteModelOperationsAdapter;
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
import maquette.development.ports.mlprojects.InMemoryMLProjectCreationPort;
import maquette.development.ports.models.InMemoryModelServing;
import maquette.operations.MaquetteModelOperations;
import maquette.operations.ports.InMemoryDeployedModelServicesRepository;

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
        var operationsAdapter = MaquetteModelOperationsAdapter.apply(runtime);
        var developmentAdapter = MaquetteModelDevelopmentAdapter.apply(runtime);

        var dataAssetsRepository = InMemoryDataAssetsRepository.apply();

        var modelServing = InMemoryModelServing.apply();
        var mlProjects = InMemoryMLProjectCreationPort.apply();

        var workspacesRepository = InMemoryWorkspacesRepository.apply();
        var modelsRepository = InMemoryModelsRepository.apply();
        var sandboxesRepository = InMemorySandboxesRepository.apply();
        var infrastructurePort = FakeInfrastructurePort.apply();
        var deployedModelServicesRepository = InMemoryDeployedModelServicesRepository.apply();

        var modelDevelopment = MaquetteModelDevelopment.apply(
            runtime, workspacesRepository, modelsRepository,
            sandboxesRepository, infrastructurePort,
            dataAssetsAdapter, operationsAdapter, modelServing, mlProjects);

        var shop = MaquetteDataShop
            .apply(dataAssetsRepository, workspacesAdapter, FakeEmailClient.apply(), FakeProvider.apply());

        var modelOperations = MaquetteModelOperations.apply(deployedModelServicesRepository, developmentAdapter);

        dataAssetsAdapter.setMaquetteModule(shop);
        workspacesAdapter.setMaquetteModule(modelDevelopment);

        return runtime
            .withModule(rt -> shop)
            .withModule(rt -> modelDevelopment)
            .withModule(rt -> modelOperations);
    }

}
