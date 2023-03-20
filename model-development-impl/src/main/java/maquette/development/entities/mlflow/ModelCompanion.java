package maquette.development.entities.mlflow;

import lombok.AllArgsConstructor;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.development.entities.mlflow.client.ModelFromRegistry;
import maquette.development.entities.mlflow.client.VersionFromRegistry;
import maquette.development.ports.ModelsRepository;
import maquette.development.values.model.ModelProperties;
import maquette.development.values.model.ModelVersion;
import maquette.development.values.model.ModelVersionStage;
import maquette.development.values.model.governance.GitDetails;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * This class bundles functionalities to merge model information retrieved from MLflow
 * with model information saved in own database.
 */
@AllArgsConstructor(staticName = "apply")
public final class ModelCompanion {

    /**
     * The id of the workspace a model belongs to.
     */
    private final UID workspaceId;

    /**
     * The model repository to access model data in own database.
     */
    private final ModelsRepository models;

    /**
     * Merges information retrieved from MLflow with information stored in database.
     *
     * @param registeredModel The model as retrieved from MLflow.
     * @return Merged model properties
     */
    public CompletionStage<ModelProperties> mapModel(ModelFromRegistry registeredModel) {
        return models
            .findModelByName(workspaceId, registeredModel.getName())
            .thenApply(maybeModel -> {
                if (maybeModel.isEmpty() || maybeModel.get()
                    .getUpdated()
                    .getAt()
                    .compareTo(registeredModel.getUpdated()) < 0) {

                    return Pair.of(
                        merge(registeredModel, maybeModel.orElse(null)),
                        maybeModel
                    );
                } else {
                    return Pair.of(maybeModel.get(), maybeModel);
                }
            })
            .thenCompose(modelProperties -> {
                /*
                 * Checks whether we need to update model information in our own database.
                 * If yes, the model information is updated in database.
                 */
                var updatedProperties = modelProperties.getLeft();
                var existingModelProperties = modelProperties.getRight();
                var updateRequired = existingModelProperties.map(p -> !p.equals(updatedProperties)).orElse(true);

                if (updateRequired) {
                    return this
                        .models
                        .insertOrUpdateModel(workspaceId, updatedProperties)
                        .thenApply(done -> updatedProperties);
                } else {
                    return CompletableFuture.completedFuture(updatedProperties);
                }
            });
    }

    private ModelProperties merge(
        ModelFromRegistry registeredModel,
        @Nullable ModelProperties modelProperties) {

        var maybeModel = Optional.ofNullable(modelProperties);
        var name = registeredModel.getName();

        var createdBy = registeredModel
            .getVersions()
            .get(registeredModel
                .getVersions()
                .size() - 1)
            .getUser();

        var created = ActionMetadata.apply(createdBy, registeredModel.getCreated());

        var updatedBy = registeredModel
            .getVersions()
            .get(0)
            .getUser();

        var updatedTime = registeredModel.getUpdated();
        var updated = ActionMetadata.apply(updatedBy, updatedTime);

        var description = "";

        var merged = maybeModel
            .orElse(ModelProperties.apply(name, description, List.of(), created, updated));

        /*
         * Find versions which have been updated or added to MLflow.
         */
        var mergedFinal = merged;
        var updatedVersions = registeredModel
            .getVersions()
            .stream()
            .filter(
                versionFromRegistry -> {
                    var existingVersion = mergedFinal.findVersion(versionFromRegistry.getVersion());

                    return existingVersion.isEmpty() ||
                        existingVersion.get().getUpdated().getAt().compareTo(versionFromRegistry.getUpdated()) < 0;
                }
            )
            .collect(Collectors.toList());

        /*
         * Fetch information for new/ updated versions and update model information.
         */
        for (var versionFromRegistry : updatedVersions) {
            var existingVersion = merged.findVersion(versionFromRegistry.getVersion());

            if (existingVersion.isEmpty()) {
                merged = merged.withVersion(mapVersion(versionFromRegistry));
            } else {
                merged = merged.withVersion(
                    existingVersion
                        .get()
                        .withFlavours(versionFromRegistry.getFlavors())
                        .withStage(ModelVersionStage.forValue(versionFromRegistry.getStage()))
                        .withExplainers(versionFromRegistry.getExplainers())
                );
            }
        }

        merged = merged
            .withVersions(merged
                .getVersions()
                .stream()
                .sorted(Comparator.comparing(m -> m.getUpdated().getAt()))
                .collect(Collectors.toList()));

        return merged;
    }

    private ModelVersion mapVersion(VersionFromRegistry versionFromRegistry) {
        var registered = ActionMetadata.apply(
            versionFromRegistry.getUser(),
            versionFromRegistry.getCreated());

        var version = ModelVersion.apply(
            versionFromRegistry.getVersion(), registered, versionFromRegistry.getFlavors(),
            ModelVersionStage.forValue(versionFromRegistry.getStage()));

        if (versionFromRegistry
            .getGitCommit()
            .isPresent()) {
            var gitDetails = GitDetails.apply(versionFromRegistry
                .getGitCommit()
                .orElse(null), versionFromRegistry
                .getGitUrl()
                .orElse(null), false);
            version = version.withGitDetails(gitDetails);
        }

        version = version.withExplainers(versionFromRegistry.getExplainers());

        return version;
    }

}
