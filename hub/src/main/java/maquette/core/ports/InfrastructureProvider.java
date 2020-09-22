package maquette.core.ports;

import maquette.core.entities.infrastructure.Container;
import maquette.core.entities.infrastructure.model.ContainerConfig;

import java.util.concurrent.CompletionStage;

public interface InfrastructureProvider {

    CompletionStage<Container> createContainer(ContainerConfig config);

}
