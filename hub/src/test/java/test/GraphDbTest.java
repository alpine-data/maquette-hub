package test;

import maquette.common.ObjectMapperFactory;
import maquette.common.Operators;
import maquette.core.entities.dependencies.Dependencies;
import maquette.core.entities.dependencies.model.*;
import maquette.core.values.UID;
import org.junit.Test;

public class GraphDbTest {

   @Test
   public void test() {
      /*
      var d = Dependencies.apply();
      var datasetA = DataAssetNode.apply(DataAssetType.DATASET, UID.apply("da7db777"));
      var datasetB = DataAssetNode.apply(DataAssetType.SOURCE, UID.apply("c213bf5d"));

      var project = ProjectNode.apply(UID.apply("34984c15"));

      var applicationA = ApplicationNode.apply(project.getProject(), UID.apply("81ed5f56"));
      var applicationB = ApplicationNode.apply(project.getProject(), UID.apply("0036664a"));
      var applicationC = ApplicationNode.apply(project.getProject(), UID.apply("d2a30f79"));

      var modelA = ModelNode.apply(project.getProject(), "ElasticnetWineModel");

      d.trackProduction(datasetA, applicationA);
      d.trackConsumption(datasetA, modelA);

      d.trackConsumption(datasetA, applicationB);
      d.trackUsage(modelA, applicationB);
      d.trackProduction(datasetB, applicationB);

      d.trackConsumption(datasetB, applicationC);

      d
         .getDependencyGraph(datasetA)
         .thenAccept(s -> {
            var om = ObjectMapperFactory.apply().create(true);
            System.out.println(Operators.suppressExceptions(() -> om.writeValueAsString(s)));
         });
       */
   }

}
