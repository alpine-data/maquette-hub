package maquette.development.specs;

import maquette.development.ports.*;
import maquette.development.ports.infrastructure.FakeInfrastructurePort;
import maquette.development.ports.infrastructure.InfrastructurePort;
import maquette.development.ports.models.FakeModelOperationsPort;
import maquette.development.ports.models.InMemoryModelServing;
import maquette.development.ports.models.ModelOperationsPort;
import maquette.development.ports.models.ModelServingPort;

public class WorkspacesSpecsTest extends WorkspacesSpecs {

    private FakeDataAssetsServicePort dataAssetsServicePort;

    private InMemoryWorkspacesRepository workspacesRepository;

    @Override
    public WorkspacesRepository setupWorkspacesRepository() {
        workspacesRepository = InMemoryWorkspacesRepository.apply();
        return workspacesRepository;
    }

    @Override
    public ModelsRepository setupModelsRepository() {
        return InMemoryModelsRepository.apply();
    }

    @Override
    public InfrastructurePort setupInfrastructurePort() {
        return FakeInfrastructurePort.apply();
    }

    @Override
    public DataAssetsServicePort setupDataAssetsServicePort() {
        dataAssetsServicePort = FakeDataAssetsServicePort.apply();
        return dataAssetsServicePort;
    }

    @Override
    public ModelOperationsPort setupModelOperationsPort() {
        return FakeModelOperationsPort.apply();
    }

    @Override
    public ModelServingPort setupModelServingPort() {
        return InMemoryModelServing.apply();
    }

    @Override
    public SandboxesRepository setupSandboxesRepository() {
        return InMemorySandboxesRepository.apply();
    }

    @Override
    protected void create_data_access_request(String accessRequestId,
                                              String dataAssetName,
                                              String workspaceName,
                                              String workspaceId) {
        dataAssetsServicePort.createDataAssetWithAccessRequest(accessRequestId, dataAssetName, workspaceName,
            workspaceId);
    }

    @Override
    protected void auto_infrastructure_is_throwing_an_error(Boolean throwError) {
        // not needed
    }

}
