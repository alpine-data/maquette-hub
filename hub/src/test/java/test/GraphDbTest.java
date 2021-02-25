package test;

import maquette.core.entities.dependencies.Dependencies;
import maquette.core.entities.dependencies.model.*;
import maquette.core.values.UID;
import org.junit.Test;

public class GraphDbTest {

   @Test
   public void test() {
      var d = Dependencies.apply();
      var datasetA = DataAssetNode.apply(DataAssetType.DATASET, UID.apply("dataset-a"));
      var datasetB = DataAssetNode.apply(DataAssetType.SOURCE, UID.apply("dataset-b"));

      var project = ProjectNode.apply(UID.apply("project"));

      var applicationA = ApplicationNode.apply(project.getProject(), UID.apply("application-a"));
      var applicationB = ApplicationNode.apply(project.getProject(), UID.apply("application-b"));
      var applicationC = ApplicationNode.apply(project.getProject(), UID.apply("application-c"));

      var modelA = ModelNode.apply(project.getProject(), "model-a");

      d.trackProduction(datasetA, applicationA);
      d.trackConsumption(datasetA, modelA);

      d.trackConsumption(datasetA, applicationB);
      d.trackUsage(modelA, applicationB);
      d.trackProduction(datasetB, applicationB);

      d.trackConsumption(datasetB, applicationC);
   }

}
