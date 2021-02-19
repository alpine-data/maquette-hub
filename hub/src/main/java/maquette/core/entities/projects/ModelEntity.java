package maquette.core.entities.projects;

import lombok.AllArgsConstructor;
import maquette.core.entities.projects.model.Model;
import maquette.core.values.UID;
import org.mlflow.tracking.MlflowClient;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public class ModelEntity {

   private final UID project;

   private final String name;

   private final String mlflowTrackingUri;

   public CompletionStage<Model> getProperties() {
      return null;
   }

}
