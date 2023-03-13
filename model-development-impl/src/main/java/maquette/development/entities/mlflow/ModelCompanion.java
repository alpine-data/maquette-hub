package maquette.development.entities.mlflow;

import lombok.AllArgsConstructor;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.development.ports.ModelsRepository;
import maquette.development.values.model.ModelProperties;
import maquette.development.values.model.ModelVersion;
import maquette.development.values.model.ModelVersionStage;
import maquette.development.values.model.governance.GitDetails;
import maquette.development.values.model.mlflow.ModelFromRegistry;
import org.apache.commons.lang3.tuple.Pair;

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
                var title = registeredModel.getName();
                var name = registeredModel.getName();
                var flavors = registeredModel
                    .getVersions()
                    .stream()
                    .flatMap(version -> version
                        .getFlavors()
                        .stream())
                    .collect(Collectors.toSet());
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

                var updatedTime = registeredModel
                    .getVersions()
                    .get(0)
                    .getCreated();
                var updated = ActionMetadata.apply(updatedBy, updatedTime);

                var description = ""; // TODO find way to get description from MLflow API.

                var versions = registeredModel
                    .getVersions()
                    .stream()
                    .map(v -> {
                        var registered = ActionMetadata.apply(
                            v.getUser(),
                            v.getCreated());

                        var version = ModelVersion.apply(
                            v.getVersion(), registered, v.getFlavors(), ModelVersionStage.forValue(v.getStage()));

                        if (v
                            .getGitCommit()
                            .isPresent()) {
                            var gitDetails = GitDetails.apply(v
                                .getGitCommit()
                                .orElse(null), v
                                .getGitUrl()
                                .orElse(null), false);
                            version = version.withGitDetails(gitDetails);
                        }

                        version = version.withExplainers(v.getExplainers());

                        return version;
                    })
                    .collect(Collectors.toList());

                var merged = maybeModel
                    .orElse(ModelProperties.apply(name, description, versions, created, updated));

                var versionsMap = merged
                    .getVersions()
                    .stream()
                    .collect(Collectors.toMap(ModelVersion::getVersion, v -> v));

                merged = merged.withVersions(versions
                    .stream()
                    .map(versionFromRegistry -> {
                        if (versionsMap.containsKey(versionFromRegistry.getVersion())) {
                            var v = versionsMap.get(versionFromRegistry.getVersion());

                            if (versionFromRegistry
                                .getGitDetails()
                                .isPresent() && v
                                .getGitDetails()
                                .isPresent() && v
                                .getGitDetails()
                                .get()
                                .isMainBranch()) {
                                v = v.withGitDetails(versionFromRegistry
                                    .getGitDetails()
                                    .get()
                                    .withMainBranch(true));
                            } else {
                                v = v.withGitDetails(versionFromRegistry
                                    .getGitDetails()
                                    .orElse(null));
                            }

                            return v
                                .withStage(versionFromRegistry.getStage())
                                .withFlavours(versionFromRegistry.getFlavours());
                        } else {
                            return versionFromRegistry;
                        }
                    })
                    .collect(Collectors.toList()));
                return Pair.of(merged, maybeModel);
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

}
