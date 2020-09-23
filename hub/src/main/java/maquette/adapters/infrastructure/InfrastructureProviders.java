package maquette.adapters.infrastructure;

import maquette.core.ports.InfrastructureProvider;

public final class InfrastructureProviders {

   private InfrastructureProviders() {

   }

   public static InfrastructureProvider create() {
      return DockerInfrastructureProvider.apply();
   }

}
