package maquette.operations.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.values.user.User;
import maquette.operations.entities.DeployedModelEntities;
import maquette.operations.value.DeployedModel;
import maquette.operations.value.DeployedModelServiceProperties;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public class DeployedModelServicesImpl implements DeployedModelServices {

    private final DeployedModelEntities deployedModelEntities;


    @Override
    public CompletionStage<Done> createDeployedModel(User user, String name, String title, String url) {
        return deployedModelEntities.registerModel(name, title, url);
    }

    @Override
    public CompletionStage<Done> createDeployedModelService(User user, String modelName,
                                                            DeployedModelServiceProperties properties) {
        return deployedModelEntities.getDeployedModelEntity(modelName)
            .thenCompose((entity) ->
                entity
                    .registerModelService(properties)
                    .thenCompose((done) ->
                        deployedModelEntities.assignService(modelName, properties.getName())));
    }

    @Override
    public CompletionStage<Optional<DeployedModel>> findDeployedModel(User user, String name) {
        return deployedModelEntities.findByName(name);
    }
}
