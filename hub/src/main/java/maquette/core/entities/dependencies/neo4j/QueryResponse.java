package maquette.core.entities.dependencies.neo4j;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class QueryResponse<T> {

   List<Result<T>> results;

   JsonNode errors;

}
