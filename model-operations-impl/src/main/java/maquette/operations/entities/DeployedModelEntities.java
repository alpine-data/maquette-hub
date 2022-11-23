package maquette.operations.entities;

import akka.Done;

import java.util.concurrent.CompletionStage;

public class DeployedModelEntities {

    /**
     * Register a new model in the model operations database.
     *
     * @param name The unique name of the model.
     * @param title The human-readable title of the model.
     * @param url The Model-Repository URL of the model.
     * @return Done.
     */
    public CompletionStage<Done> registerModel(String name, String title, String url) {
        return null;
    }

}
