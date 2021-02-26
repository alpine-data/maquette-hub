package maquette.core.entities.dependencies.neo4j;

import akka.japi.Function;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import maquette.common.Operators;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Graph<T> {

   List<Node<T>> nodes;

   List<Relationship> relationships;

   public static <T> Graph<T> apply() {
      return apply(List.of(), List.of());
   }

   public Graph<T> combine(Graph<T> other) {
      return Graph.apply(
         Stream
            .concat(nodes.stream(), other.nodes.stream())
            .distinct()
            .collect(Collectors.toList()),
         Stream
            .concat(relationships.stream(), other.relationships.stream())
            .distinct()
            .collect(Collectors.toList()));
   }

   public <U> Graph<U> map(Function<T, U> mapping) {
      var nodes = this.nodes
         .stream()
         .map(node -> node.withProperties(Operators.suppressExceptions(() -> mapping.apply(node.properties))))
         .collect(Collectors.toList());

      return apply(nodes, relationships);
   }

   public <U> Graph<U> withNodes(List<Node<U>> nodes) {
      return apply(nodes, relationships);
   }

}
