package maquette.operations.ports;

import akka.Done;
import maquette.operations.value.DeployedModel;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletionStage;

public interface DeployedModelsRepository {
    CompletionStage<Optional<DeployedModel>> findByName(String name);

    CompletionStage<Done> insertOrUpdate(String name, String title, String url);

    CompletionStage<Set<String>> findServiceReferences(String name);

    CompletionStage<Done> assignServices(String name, Set<String> serviceName);

    CompletionStage<Done> removeServices(String name, Set<String> serviceNames);
}
