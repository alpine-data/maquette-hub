package maquette.core.entities.dependencies.neo4j;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Result<T> {

   List<String> columns;

   List<ResultSet<T>> data;

}
