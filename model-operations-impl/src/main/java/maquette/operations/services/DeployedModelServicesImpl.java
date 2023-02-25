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
public final class DeployedModelServicesImpl implements DeployedModelServices {

    private final DeployedModelEntities deployedModelEntities;


    @Override
    public CompletionStage<Done> createDeployedModel(User user, String name, String title, String url) {
        return deployedModelEntities.registerModel(name, title, url);
    }

    @Override
    public CompletionStage<Done> createDeployedModelService(User user, String modelUrl,
                                                            DeployedModelServiceProperties properties) {
        return deployedModelEntities.getDeployedModelEntity(modelUrl)
            .thenCompose((entity) ->
                entity
                    .registerModelService(properties)
                    .thenCompose((done) ->
                        deployedModelEntities.assignService(modelUrl, properties.getName())));
    }

    @Override
    public CompletionStage<Done> registerModelServiceInstance(User user, DeployedModelProperties model,
                                                              DeployedModelServiceProperties service,
                                                              DeployedModelServiceInstanceProperties instance) {
        return deployedModelEntities
            .findByUrl(model.getMlflowInstanceId())
            .thenCompose(maybeModel -> {
                if (maybeModel.isEmpty()) {
                    return deployedModelEntities
                        .registerModel(model.getMlflowInstanceId(), model.getName(), model.getMlflowInstanceId())
                        .thenCompose(done -> deployedModelEntities.getDeployedModelEntity(model.getName()));
                } else {
                    return CompletableFuture.completedFuture(maybeModel.get());
                }
            })
            .thenCompose(entity -> entity.registerModelServiceInstance(service, instance));
    }

    @Override
    public CompletionStage<Optional<DeployedModel>> findDeployedModel(User user, String url) {
        /* TODO: Implement properly
        return deployedModelEntities.findByUrl(url);
         */

        return null;
    }
}
