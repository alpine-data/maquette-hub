package test;

import maquette.Application;
import maquette.core.entities.dependencies.Dependencies;
import maquette.core.entities.dependencies.model.ApplicationNode;
import maquette.core.entities.dependencies.model.DataAssetNode;
import maquette.core.values.UID;
import org.jdbi.v3.core.Jdbi;
import org.junit.Test;

public class GraphDbTest {

   @Test
   public void test() {
      /*
      var connectionString = "jdbc:neo4j:bolt://localhost:7687";
      var query = "CREATE (p\\\\:Person { id: '1234', foo: 'bar' })-[\\\\:LIKES]->(t\\\\:Animals)";
      Jdbi jdbi = Jdbi.create(connectionString, "neo4j", "password");

      System.out.println(query);

      jdbi
         .withHandle(handle -> handle
            .createUpdate(query)
            .execute());

       */

      var deps = Dependencies.apply();
      deps.trackConsumption(
         DataAssetNode.apply("dataset", UID.apply()),
         ApplicationNode.apply(UID.apply(), UID.apply()));
   }

}
