package maquette.core.entities.dependencies.neo4j;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Node<T> {

   String id;

   List<String> labels;

   T properties;

   public <U> Node<U> withProperties(U properties) {
      return apply(id, labels, properties);
   }

}
