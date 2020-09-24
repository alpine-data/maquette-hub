package maquette.core.entities.infrastructure.model;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Optional;

@Value
@AllArgsConstructor(staticName = "apply")
public class PortSpec {

   Integer containerPort;

   Integer hostPort;

   public static PortSpec apply(int containerPort) {
      return apply(containerPort, null);
   }

   public Optional<Integer> getHostPort() {
      return Optional.ofNullable(hostPort);
   }

}
