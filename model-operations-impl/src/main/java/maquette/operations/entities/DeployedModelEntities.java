package maquette.operations.entities;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.operations.ports.DeployedModelServicesRepository;
import maquette.operations.ports.DeployedModelsRepository;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public class DeployedModelEntities {

    private final DeployedModelsRepository deployedModelsRepository;

    private final DeployedModelServicesRepository deployedModelServicesRepository;

    /**
     * Register a new model in the model operations database.
     *
     * @param name  The unique name of the model.
     * @param title The human-readable title of the model.
     * @param url   The Model-Repository URL of the model.
     * @return Done.
     */
    public CompletionStage<Done> registerModel(String name, String title, String url) {
        return deployedModelsRepository.insertOrUpdate(name, title, url);
    }

    /**
     * Find the model by name, including its services nd instances
     *
     * @param modelUrl The URL of the model.
     * @return Model.
     */
    public CompletionStage<Optional<DeployedModelEntity>> findByUrl(String modelUrl) {
        /* Minor refactoring required.

        return deployedModelsRepository
            .findByUrl(modelUrl)
            .thenCompose(mdl -> mdl.map(model ->
                    deployedModelsRepository
                        .findServiceReferences(model.getUrl())
                        .thenCompose(references ->
                            Operators
                                .allOf(references.stream().map(deployedModelServicesRepository::findByName))
                                .thenApply(refs -> refs.stream()
                                    .filter(Optional::isPresent)
                                    .map(Optional::get)
                                    .collect(Collectors.toList()))
                                .thenCompose(refs ->
                                    Operators.allOf(refs.stream()
                                        .map(ref ->
                                            deployedModelServicesRepository.findAllInstances(ref.getName())
                                                .thenApply(inst -> DeployedModelService.apply(ref, inst))
                                        )
                                    ).thenApply(model::withServices)
                                )
                        )
                ).orElse(CompletableFuture.completedFuture(null))
            ).thenApply(Optional::ofNullable);
         */

        return null;
    }

    /**
     * Assign service to deployed model
     *
     * @param modelUrl   The URL of model.
     * @param serviceName The name of service to be assigned.
     * @return Done.
     */
    public CompletionStage<Done> assignService(String modelUrl, String serviceName) {
        return deployedModelsRepository.assignServices(modelUrl, Set.of(serviceName));
    }

    /**
     * Get entity of the deployed model
     * @param modelUrl  The url of the model
     * @return Model entity.
     */
    public CompletionStage<DeployedModelEntity> getDeployedModelEntity(String modelUrl) {
        return CompletableFuture.completedFuture(DeployedModelEntity.apply(modelUrl, deployedModelServicesRepository));
    }

}
