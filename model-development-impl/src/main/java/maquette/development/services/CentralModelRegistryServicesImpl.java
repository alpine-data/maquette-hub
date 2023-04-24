package maquette.development.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.values.user.SystemUser;
import maquette.core.values.user.User;
import maquette.development.entities.ModelEntities;
import maquette.development.entities.WorkspaceEntities;
import maquette.development.entities.WorkspaceEntity;
import maquette.development.ports.infrastructure.InfrastructurePort;
import maquette.development.values.WorkspaceMemberRole;
import maquette.development.values.model.ModelProperties;
import maquette.development.values.stacks.StackRuntimeState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public class CentralModelRegistryServicesImpl implements CentralModelRegistryServices {
    WorkspaceEntities cmrWorkspaces;
    WorkspaceEntities workspaces;
    InfrastructurePort infrastructurePort;

    private CompletionStage<WorkspaceEntity> initializeWorkspace() {
        var user = SystemUser.apply();
        return
            cmrWorkspaces
            .createWorkspace(
                user,
                REGISTRY_WORKSPACE,
                "Central model registry",
                "Central model registry"
            )
            .thenCompose(project -> cmrWorkspaces.getWorkspaceById(project.getId()))
            .thenCompose(workspace -> {
                var adminAddedCS = workspace
                    .members()
                    .addMember(user, user.toAuthorization(), WorkspaceMemberRole.ADMIN);

                var mlFlowInitializedCS = workspace.initializeMlflowEnvironment("", false);

                return Operators.compose(
                    adminAddedCS, mlFlowInitializedCS,
                    (adminAdded, mlFlowInitialized) -> workspace
                );
            });
    }

    private CompletionStage<WorkspaceEntity> getWorkspace() {
        return cmrWorkspaces
            .findWorkspaceByName(REGISTRY_WORKSPACE)
            .thenCompose(maybeWorkspace -> {
                if (maybeWorkspace.isPresent())
                    return CompletableFuture.completedFuture(maybeWorkspace.get());
                return initializeWorkspace();
            });
    }

    @Override
    public CompletionStage<Done> initialize() {
        return getWorkspace().thenApply(workspace -> Done.getInstance());
    }

    @Override
    public CompletionStage<List<ModelProperties>> getModels(User user, String search) {
        return getWorkspace()
            .thenCompose(WorkspaceEntity::getModels)
            .thenCompose(ModelEntities::getModels)
            .thenApply(results -> results.stream()
                .filter(result -> isSearchMatch(result, search))
                .sorted((o1, o2) -> {
                    // sort by relevance, match in name has priority
                    int score1 =
                              (o1.getName().toLowerCase().contains(search.toLowerCase()) ? 2 : 0)
                            + (o1.getDescription().toLowerCase().contains(search.toLowerCase()) ? 1 : 0);
                    int score2 =
                              (o2.getName().toLowerCase().contains(search.toLowerCase()) ? 2 : 0)
                            + (o2.getDescription().toLowerCase().contains(search.toLowerCase()) ? 1 : 0);
                    return -Integer.compare(score1, score2);
                })
                .collect(Collectors.toList())
            );
    }

    @Override
    public CompletionStage<Done> importModel(User user, String workspaceName, String modelName, String version) {
        var sourceCS = workspaces.getWorkspaceByName(workspaceName);
        var destinationCS = getWorkspace();
        return
            Operators.compose(
                sourceCS,
                destinationCS,
                (source, destination) ->
                    Operators.compose(
                        source.getMlflowStatus(),
                        source.getModels().thenApply(mdl -> mdl.getModel(modelName)),
                        destination.getMlflowStatus(), (sourceMlflow, sourceModel, destinationMlflow) -> {
                            if(sourceMlflow.isEmpty()) {
                                throw new RuntimeException("source workspace mlflow stack configuration is empty");
                            }
                            if(destinationMlflow.isEmpty()) {
                                throw new RuntimeException("destination workspace mlflow stack configuration is empty");
                            }

                            var sourceEnv = getMlflowParams(sourceMlflow.get());
                            var destinationEnv = getMlflowParams(destinationMlflow.get());

                            return infrastructurePort.importModel(
                                source.getId(),
                                modelName,
                                version,
                                destination.getId(),
                                createModelName(workspaceName, modelName),
                                workspaceName,
                                sourceEnv,
                                destinationEnv);
                        })
            )
            // unwrap all the futures
            .thenCompose(fut -> fut)
            .thenCompose(fut -> fut);
    }

    /**
     * Create name of model in central registry
     * @param workspace workspace name
     * @param modelName source model name
     * @return destination model name
     */
    private String createModelName(String workspace, String modelName) {
        return workspace + "--" + modelName;
    }

    private boolean isSearchMatch(ModelProperties model, String query) {
        if(query == null || query.isEmpty()) return true;
        return     model.getName().toLowerCase().contains(query.toLowerCase())
                || model.getDescription().toLowerCase().contains(query.toLowerCase());
    }

    private Map<String, String> getMlflowParams(StackRuntimeState stackRuntimeState) {
        var map = stackRuntimeState.getParameters().getParameters();
        var newMap = new HashMap<>(map);
        if(newMap.containsKey("INTERNAL_MLFLOW_TRACKING_URI")) {
            newMap.put("MLFLOW_TRACKING_URI", newMap.get("INTERNAL_MLFLOW_TRACKING_URI"));
        }
        return newMap;
    }
}
