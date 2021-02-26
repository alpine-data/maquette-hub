package maquette.core.entities.dependencies.neo4j;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Statement {

   String statement;

   List<String> resultDataContents;

   public static Statement apply(String statement) {
      return apply(statement, List.of("graph"));
   }

}
