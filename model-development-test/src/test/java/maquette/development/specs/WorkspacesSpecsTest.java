package maquette.development.specs;

import maquette.development.ports.*;
import maquette.development.ports.infrastructure.FakeInfrastructurePort;
import maquette.development.ports.infrastructure.InfrastructurePort;

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
    public SandboxesRepository setupSandboxesRepository() {
        return InMemorySandboxesRepository.apply();
    }

    @Override
    protected void create_data_access_request(String accessRequestId,
                                                       String dataAssetName,
                                                       String workspaceName,
                                                       String workspaceId) {
        dataAssetsServicePort.createDataAssetWithAccessRequest(accessRequestId, dataAssetName, workspaceName, workspaceId);
    }

}
