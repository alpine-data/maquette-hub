package maquette.core.entities.dependencies.neo4j;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Relationship {

   String id;

   String type;

   String startNode;

   String endNode;

   JsonNode properties;

}
