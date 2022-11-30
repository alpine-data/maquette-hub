package maquette.operations.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.values.user.User;
import maquette.operations.entities.DeployedModelEntities;
import maquette.operations.value.DeployedModel;
import maquette.operations.value.DeployedModelProperties;
import maquette.operations.value.DeployedModelServiceInstanceProperties;
import maquette.operations.value.DeployedModelServiceProperties;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
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
    public CompletionStage<Done> registerModelServiceInstance(User user, DeployedModelProperties model,
                                                              DeployedModelServiceProperties service,
                                                              DeployedModelServiceInstanceProperties instance) {
        return deployedModelEntities
            .findByName(model.getUrl())
            .thenCompose(maybeModel -> {
                if (maybeModel.isEmpty()) {
                    return deployedModelEntities
                        .registerModel(model.getUrl(), model.getName(), model.getUrl())
                        .thenCompose(done -> deployedModelEntities.getDeployedModelEntity(model.getName()));
                } else {
                    return CompletableFuture.completedFuture(maybeModel.get());
                }
            })
            .thenCompose(entity -> entity.registerModelServiceInstance(service, instance));
    }

    @Override
    public CompletionStage<Optional<DeployedModel>> findDeployedModel(User user, String name) {
        /* TODO: Refactor. DeployedModel should be returned form DeployedModelEntity.

        return deployedModelEntities
            .findByName(name)
            .thenApply(entity -> {
                if (entity.isPresent()) {
                    //
                }
            });

        */

        return null;
    }
}
