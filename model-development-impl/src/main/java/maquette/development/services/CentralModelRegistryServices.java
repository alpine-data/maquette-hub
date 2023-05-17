package maquette.development.services;

import akka.Done;
import maquette.core.values.user.User;
import maquette.development.values.EnvironmentType;
import maquette.development.values.WorkSpaceMlflowView;
import maquette.development.values.model.Model;
import maquette.development.values.model.ModelProperties;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public interface CentralModelRegistryServices {
    String REGISTRY_WORKSPACE = "central-model-registry";


    public CompletionStage<Model> getModel(User user, String modelName);

    /**
     * List all models that match a search query in central model registry
     *
     * @param user   executing user
     * @param search search query
     * @return list of all models
     */
    CompletionStage<List<ModelProperties>> getModels(User user, String search);

    CompletionStage<String> getExplainer(User user, String workspace, String model, String version, String artifactPath);

    CompletionStage<WorkSpaceMlflowView> getWorkspaceForView();

    /**
     * Import a model from a workspace to the central model registry
     *
     * @param user      executing user
     * @param workspace workspace where model exists
     * @param model     model name
     * @param version   model version
     * @return done
     */
    CompletionStage<Done> importModel(User user, String workspace, String model, String version);

    /**
     * Initialize central model registry
     *
     * @return done
     */
    CompletionStage<Done> initialize();

    static boolean manages(String workspace) {
        return REGISTRY_WORKSPACE.equals(workspace);
    }

    /**
     * Get environment variables/ properties for a workspace.
     *
     * @param user            The user who requests the environment.
     * @param workspace       The name of the workspace for which the environment should be provided.
     * @param environmentType The type of the environment.
     * @param returnBase64    If true, all environment values are returned as Base64 string. Under some circumstances
     *                        this is required, e.g. if Azure Storage Authentication keys are shared.
     *                        If unsure, enable `returnBase64` and decode the response on client-side.
     * @return The environment variables for the workspace.
     */
    CompletionStage<Map<String, String>> getEnvironment(User user, String workspace, EnvironmentType environmentType,
                                                        boolean returnBase64);
}
