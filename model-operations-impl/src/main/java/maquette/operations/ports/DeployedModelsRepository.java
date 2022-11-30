package maquette.operations.ports;

import akka.Done;
import maquette.operations.value.DeployedModel;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletionStage;

public interface DeployedModelsRepository {
    CompletionStage<List<DeployedModel>> findByName(String name);
    CompletionStage<Optional<DeployedModel>> findByUrl(String url);

    CompletionStage<Done> insertOrUpdate(String name, String title, String url);

    CompletionStage<Set<String>> findServiceReferences(String modelUrl);

    CompletionStage<Done> assignServices(String modelUrl, Set<String> serviceName);

    CompletionStage<Done> removeServices(String modelUrl, Set<String> serviceNames);
}
