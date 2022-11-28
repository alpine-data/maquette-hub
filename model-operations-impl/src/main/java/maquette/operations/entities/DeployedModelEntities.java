package maquette.operations.entities;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.operations.ports.DeployedModelServicesRepository;
import maquette.operations.ports.DeployedModelsRepository;
import maquette.operations.value.DeployedModel;
import maquette.operations.value.DeployedModelService;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

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
     * @param name The name of the model.
     * @return Model.
     */
    public CompletionStage<Optional<DeployedModel>> findByName(String name) {
        return deployedModelsRepository
            .findByName(name)
            .thenCompose(mdl -> mdl.map(model ->
                    deployedModelsRepository
                        .findServiceReferences(model.getName())
                        .thenCompose(references ->
                            Operators.allOf(references.stream().map(deployedModelServicesRepository::findByName))
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
    }

    /**
     * Assign service to deployed model
     *
     * @param modelName   The name of model.
     * @param serviceName The name of service to be assigned.
     * @return Done.
     */
    public CompletionStage<Done> assignService(String modelName, String serviceName) {
        return deployedModelsRepository.assignServices(modelName, Set.of(serviceName));
    }

}
