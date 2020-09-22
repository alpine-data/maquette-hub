package maquette.core.ports;

import akka.Done;
import maquette.core.entities.infrastructure.model.DeploymentMemento;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface InfrastructureRepository {

    CompletionStage<Done> insertOrUpdateDeployment(DeploymentMemento memento);

    CompletionStage<Done> removeDeployment(String name);

    CompletionStage<List<DeploymentMemento>> getDeployments();

}
