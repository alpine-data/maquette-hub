package maquette.development.entities;

import akka.Done;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;
import maquette.core.common.Operators;
import maquette.core.ports.MembersCompanion;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;
import maquette.development.ports.ModelsRepository;
import maquette.development.ports.WorkspacesRepository;
import maquette.development.ports.infrastructure.InfrastructurePort;
import maquette.development.ports.models.ModelServingPort;
import maquette.development.values.EnvironmentType;
import maquette.development.values.WorkspaceMemberRole;
import maquette.development.values.WorkspaceProperties;
import maquette.development.values.exceptions.VolumeAlreadyExistsException;
import maquette.development.values.exceptions.VolumeDoesntExistException;
import maquette.development.values.sandboxes.volumes.ExistingVolume;
import maquette.development.values.sandboxes.volumes.NewVolume;
import maquette.development.values.sandboxes.volumes.VolumeDefinition;
import maquette.development.values.stacks.MlflowStackConfiguration;
import maquette.development.values.stacks.StackRuntimeState;
import maquette.development.values.stacks.VolumeProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor(staticName = "apply")
public final class WorkspaceEntity {

    private static final Logger LOG = LoggerFactory.getLogger(WorkspaceEntity.class);

    public static final String MLFLOW_INSTANCE_PREFIX = "mlflow--";

    private final UID id;

    private final WorkspacesRepository repository;

    private final ModelsRepository models;

    private final ModelServingPort modelServingPort;

    private final InfrastructurePort infrastructurePort;

    public MembersCompanion<WorkspaceMemberRole> members() {
        return MembersCompanion.apply(id, repository);
    }

    public CompletionStage<Boolean> isMember(User user) {
        return isMember(user, null);
    }

    public CompletionStage<Boolean> isMember(User user, WorkspaceMemberRole role) {
        return members()
            .getMembers()
            .thenApply(members -> members
                .stream()
                .anyMatch(granted -> granted
                    .getAuthorization()
                    .authorizes(user) && (Objects.isNull(role) || granted
                    .getRole()
                    .equals(role))));
    }

    public CompletionStage<Done> updateProperties(User executor, String name, String title, String summary) {
        // TODO mw: value validation ...

        return getProperties()
            .thenCompose(properties -> {
                var updated = properties
                    .withName(name)
                    .withTitle(title)
                    .withSummary(summary)
                    .withModified(ActionMetadata.apply(executor));

                return repository.insertOrUpdateWorkspace(updated);
            });
    }

    public CompletionStage<Done> initializeMlflowEnvironment() {
        var config = MlflowStackConfiguration.apply(
            getMlflowStackName(id),
            Instant
                .now()
                .plus(5, ChronoUnit.DAYS),
            Lists.newArrayList(getWorkspaceResourceGroupName()),
            Maps.newHashMap());

        return infrastructurePort
            .createOrUpdateStackInstance(id, config)
            .thenCompose(done -> getProperties())
            .thenCompose(properties -> repository.insertOrUpdateWorkspace(properties.withMlFlowConfiguration(config)));
    }

    public CompletionStage<Map<String, String>> getEnvironment(User user, EnvironmentType environmentType) {
        return infrastructurePort
            .getInstanceParameters(id, getMlflowStackName(id))
            .thenApply(parameters -> {
                Map<String, String> result = Maps.newHashMap();
                result.putAll(parameters.getParameters());


                if (environmentType.equals(EnvironmentType.SANDBOX)) {
                    /*
                     * If environment is requested from an internal sandbox environment, we need to change the
                     * correct MLflow endpoint urls.
                     */
                    if (result.containsKey(MlflowStackConfiguration.PARAM_INTERNAL_MLFLOW_TRACKING_URL)) {
                        result.put(
                            MlflowStackConfiguration.PARAM_MLFLOW_TRACKING_URL,
                            result.get(MlflowStackConfiguration.PARAM_INTERNAL_MLFLOW_TRACKING_URL));
                    }

                    if (result.containsKey(MlflowStackConfiguration.PARAM_INTERNAL_MLFLOW_ENDPOINT)) {
                        result.put(
                            MlflowStackConfiguration.PARAM_MLFFLOW_ENDPOINT,
                            result.get(MlflowStackConfiguration.PARAM_INTERNAL_MLFLOW_ENDPOINT));
                    }

                    if (result.containsKey(MlflowStackConfiguration.PARAM_INTERNAL_MLFLOW_S3_ENDPOINT_URL)) {
                        result.put(
                            MlflowStackConfiguration.PARAM_MLFLOW_S3_ENDPOINT_URL,
                            result.get(MlflowStackConfiguration.PARAM_INTERNAL_MLFLOW_S3_ENDPOINT_URL));
                    }
                }

                /*
                 * Add username information used by MLflow.
                 */
                if (user instanceof AuthenticatedUser) {
                    result.put("LOGNAME", ((AuthenticatedUser) user).getId().getValue());
                }

                return result;
            });
    }

    public CompletionStage<Optional<StackRuntimeState>> getMlflowStatus() {
        return getProperties()
            .thenCompose(properties -> {
                if (properties
                    .getMlFlowConfiguration()
                    .isPresent()) {
                    var config = properties
                        .getMlFlowConfiguration()
                        .get();

                    var statusCS = infrastructurePort
                        .getStackInstanceStatus(config.getStackInstanceName());

                    var paramsCS = infrastructurePort
                        .getInstanceParameters(id, config.getStackInstanceName());

                    return Operators.compose(statusCS, paramsCS, (status, params) ->
                        Optional.of(StackRuntimeState.apply(config, status, params)));
                } else {
                    return CompletableFuture.completedFuture(Optional.empty());
                }
            });
    }

    public CompletionStage<WorkspaceProperties> getProperties() {
        return repository.getWorkspaceById(id);
    }

    public CompletionStage<ModelEntities> getModels() {
        return getProperties()
            .thenCompose(properties -> {
                if (properties
                    .getMlFlowConfiguration()
                    .isPresent()) {
                    return infrastructurePort
                        .getInstanceParameters(this.getId(), getMlflowStackName(id))
                        .thenApply(params -> {
                            LOG.info("Stack instance parameters for workspace `{}`: {}", id, params);

                            return properties
                                .getMlFlowConfiguration()
                                .get()
                                .getMlflowConfiguration(params);
                        })
                        .thenApply(optMlflowConfiguration -> optMlflowConfiguration
                            .map(mlflowConfiguration -> {
                                LOG.info("Current MLflow configuration for workspace `{}`: {}", id, mlflowConfiguration);

                                return ModelEntities.apply(id, mlflowConfiguration, models,
                                    modelServingPort);
                            })
                            .orElseGet(() -> {
                                LOG.warn("No MLflow configuration available for workspace `{}`.", id);
                                return ModelEntities.noMlflowBackend(id);
                            }));
                } else {
                    return CompletableFuture.completedFuture(ModelEntities.noMlflowBackend(id));
                }
            });
    }

    public static String getMlflowStackName(UID id) {
        return String.format("%s%s", MLFLOW_INSTANCE_PREFIX, id.getValue());
    }

    private String getWorkspaceResourceGroupName() {
        return String.format("workspaces--%s", this.id);
    }

    public CompletionStage<VolumeProperties> createVolume(User executor, VolumeDefinition volume) {
        return getProperties()
            .thenApply(properties -> {
                var volumes = properties
                    .getVolumes();
                if (volume instanceof NewVolume) {
                    var newVolume = (NewVolume) volume;
                    if (volumes != null && volumes
                        .stream()
                        .anyMatch(v -> v
                            .getName()
                            .equals(newVolume.getName()))) {
                        throw VolumeAlreadyExistsException.apply(newVolume.getName(), id.getValue());
                    } else {
                        var volumeConfiguration = VolumeProperties.apply(UID.apply(),
                            UID.apply(executor.getDisplayName()), newVolume.getName(), "5Gi");
                        // case for workspaces created before volumes feature
                        if (volumes == null)
                            volumes = Lists.newArrayList();
                        volumes.add(volumeConfiguration);
                        var updated = properties
                            .withVolumes(volumes)
                            .withModified(ActionMetadata.apply(executor));
                        repository.insertOrUpdateWorkspace(updated);
                        return volumeConfiguration;
                    }
                } else if (volume instanceof ExistingVolume) {
                    var existingVolume = (ExistingVolume) volume;
                    return volumes
                        .stream()
                        .filter(v -> v
                            .getName()
                            .equals(existingVolume.getName()))
                        .findFirst()
                        .orElseThrow(() -> VolumeDoesntExistException.apply(existingVolume.getName(), id.getValue()));
                } else {
                    throw new IllegalArgumentException("Not implemented");
                }
            });
    }

    public CompletionStage<List<VolumeProperties>> getVolumes(User user) {
        return getProperties()
            .thenApply(WorkspaceProperties::getVolumes)
            .thenApply(volumes ->
                volumes
                    .stream()
                    .filter(volume -> volume
                        .getUser()
                        .getValue()
                        .equals(user.getDisplayName()))
                    .collect(Collectors.toList())
            );
    }
}
