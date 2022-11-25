package maquette.operations.ports;

import akka.Done;
import maquette.operations.value.DeployedModel;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface DeployedModelsRepository {
    CompletionStage<Optional<DeployedModel>> findByName(String name);

    CompletionStage<Done> insertOrUpdate(String name, String title, String url);
}
