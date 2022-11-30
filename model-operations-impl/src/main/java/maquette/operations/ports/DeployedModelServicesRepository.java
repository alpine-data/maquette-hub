package maquette.operations.ports;

import akka.Done;
import maquette.operations.value.DeployedModelServiceInstance;
import maquette.operations.value.DeployedModelServiceProperties;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface DeployedModelServicesRepository {

    CompletionStage<Optional<DeployedModelServiceProperties>> findByName(String serviceName);

    CompletionStage<Done> insertOrUpdate(DeployedModelServiceProperties deployedModelServiceProperties);

    CompletionStage<Done> removeByName(String serviceName);

    CompletionStage<List<DeployedModelServiceInstance>> findAllInstances(String serviceName);

    CompletionStage<Done> removeAllInstances(String serviceName);

    CompletionStage<Optional<DeployedModelServiceInstance>> findInstanceByUrl(String serviceName, String url);

    CompletionStage<Done> insertOrUpdateInstance(String serviceName, DeployedModelServiceInstance instance);
}
