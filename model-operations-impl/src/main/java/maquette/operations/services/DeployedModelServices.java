package maquette.operations.services;

import akka.Done;
import maquette.core.values.user.User;
import maquette.operations.value.DeployedModel;
import maquette.operations.value.DeployedModelServiceProperties;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface DeployedModelServices {

    CompletionStage<Done> createDeployedModel(User user, String name, String title, String url);

    CompletionStage<Done> createDeployedModelService(User user, String modelName,
                                                     DeployedModelServiceProperties properties);

    CompletionStage<Optional<DeployedModel>> findDeployedModel(User user, String name);
}
