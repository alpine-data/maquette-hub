package maquette.development.specs;

import maquette.development.ports.*;

public class WorkspacesSpecsTest extends WorkspacesSpecs {

    private FakeDataAssetsServicePort dataAssetsServicePort;

    @Override
    public WorkspacesRepository setupWorkspacesRepository() {
        return InMemoryWorkspacesRepository.apply();
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
    protected void there_is_$_access_request_for_$_data_asset_with_within_$_workspace(String accessRequestName,
                                                                                      String dataAssetName,
                                                                                      String workspaceName) {
        dataAssetsServicePort.createAccessRequest(accessRequestName, dataAssetName, workspaceName);
    }

    @Override
    protected void there_is_$_data_asset(String dataAssetName) {
        dataAssetsServicePort.createDataAsset(dataAssetName);
    }

}
