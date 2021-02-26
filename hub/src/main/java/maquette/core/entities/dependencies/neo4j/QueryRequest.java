package maquette.core.entities.dependencies.neo4j;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class QueryRequest {

   List<Statement> statements;

   public static QueryRequest apply(String statement) {
      return apply(List.of(Statement.apply(statement)));
   }

}
