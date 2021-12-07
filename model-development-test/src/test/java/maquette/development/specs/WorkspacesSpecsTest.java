package maquette.development.specs;

import maquette.development.ports.*;

public class WorkspacesSpecsTest extends WorkspacesSpecs {

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

}
