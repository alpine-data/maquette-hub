package maquette.core.entities.dependencies.neo4j;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResultSet<T> {

   Graph<T> graph;

}
