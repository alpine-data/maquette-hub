package maquette.development.services;

import akka.Done;
import maquette.core.values.user.User;
import maquette.development.values.model.ModelProperties;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface CentralModelRegistryServices {
    String REGISTRY_WORKSPACE = "central-model-registry";

    /**
     * List all models that match a search query in central model registry
     *
     * @param user   executing user
     * @param search search query
     * @return list of all models
     */
    CompletionStage<List<ModelProperties>> getModels(User user, String search);

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
     * @return done
     */
    CompletionStage<Done> initialize();

    static boolean manages(String workspace) {
        return REGISTRY_WORKSPACE.equals(workspace);
    }
}
