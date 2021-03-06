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
import maquette.core.values.user.User;
import maquette.development.ports.ModelsRepository;
import maquette.development.ports.WorkspacesRepository;
import maquette.development.ports.infrastructure.InfrastructurePort;
import maquette.development.values.EnvironmentType;
import maquette.development.values.WorkspaceMemberRole;
import maquette.development.values.WorkspaceProperties;
import maquette.development.values.stacks.MlflowStackConfiguration;
import maquette.development.values.stacks.StackRuntimeState;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Getter
@AllArgsConstructor(staticName = "apply")
public final class WorkspaceEntity {

    private final UID id;

    private final WorkspacesRepository repository;

    private final ModelsRepository models;

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
                .anyMatch(granted -> granted.getAuthorization()
                    .authorizes(user) && (Objects.isNull(role) || granted.getRole().equals(role))));
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
            Instant.now().plus(24, ChronoUnit.HOURS),
            Lists.newArrayList(getWorkspaceResourceGroupName()),
            Maps.newHashMap());

        return infrastructurePort
            .createOrUpdateStackInstance(id, config)
            .thenCompose(done -> getProperties())
            .thenCompose(properties -> repository.insertOrUpdateWorkspace(properties.withMlFlowConfiguration(config)));
    }

    public CompletionStage<Map<String, String>> getEnvironment(EnvironmentType environmentType) {
        return infrastructurePort.getInstanceParameters(id, getMlflowStackName(id)).thenApply(parameters -> {
            Map<String, String> result = Maps.newHashMap();
            result.putAll(parameters.getParameters());


            if (environmentType.equals(EnvironmentType.SANDBOX)) {
                /*
                 * If environment is requested from an internal sandbox environment, we need to change the correct MLflow endpoint urls.
                 */
                if (result.containsKey(MlflowStackConfiguration.PARAM_INTERNAL_MLFLOW_TRACKING_URL)) {
                    result.put(MlflowStackConfiguration.PARAM_MLFLOW_TRACKING_URL, result.get(MlflowStackConfiguration.PARAM_INTERNAL_MLFLOW_TRACKING_URL));
                }

                if (result.containsKey(MlflowStackConfiguration.PARAM_INTERNAL_MLFLOW_ENDPOINT)) {
                    result.put(MlflowStackConfiguration.PARAM_MLFFLOW_ENDPOINT, result.get(MlflowStackConfiguration.PARAM_INTERNAL_MLFLOW_ENDPOINT));
                }
            }

            return result;
        });
    }

    public CompletionStage<Optional<StackRuntimeState>> getMlflowStatus() {
        return getProperties()
            .thenCompose(properties -> {
                if (properties.getMlFlowConfiguration().isPresent()) {
                    var config = properties.getMlFlowConfiguration().get();

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
                if (properties.getMlFlowConfiguration().isPresent()) {
                    return infrastructurePort
                        .getInstanceParameters(this.getId(), getMlflowStackName(id))
                        .thenApply(params -> properties.getMlFlowConfiguration().get().getMlflowConfiguration(params))
                        .thenApply(optMlflowConfiguration -> optMlflowConfiguration
                            .map(mlflowConfiguration -> ModelEntities.apply(id, mlflowConfiguration, models))
                            .orElse(ModelEntities.noMlflowBackend(id)));
                } else {
                    return CompletableFuture.completedFuture(ModelEntities.noMlflowBackend(id));
                }
            });
    }

    public static String getMlflowStackName(UID id) {
        return String.format("mlflow--%s", id.getValue());
    }

    private String getWorkspaceResourceGroupName() {
        return String.format("workspaces--%s", this.id);
    }

}
