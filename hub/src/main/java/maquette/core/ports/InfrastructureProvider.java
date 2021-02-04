package maquette.core.ports;

import maquette.core.entities.infrastructure.Container;
import maquette.core.entities.infrastructure.Deployment;
import maquette.core.entities.infrastructure.model.ContainerConfig;
import maquette.core.entities.infrastructure.model.DeploymentConfig;

import java.util.concurrent.CompletionStage;

public interface InfrastructureProvider {

    CompletionStage<Deployment> runDeployment(DeploymentConfig config);

    CompletionStage<Container> runContainer(ContainerConfig config);

}
