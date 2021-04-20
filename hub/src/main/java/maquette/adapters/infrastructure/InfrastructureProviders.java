package maquette.adapters.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.ConfigFactory;
import maquette.config.FileSystemRepositoryConfiguration;
import maquette.core.entities.infrastructure.ports.InfrastructureProvider;

public final class InfrastructureProviders {

   private InfrastructureProviders() {

   }

   public static InfrastructureProvider create(ObjectMapper om) {
      var cfg = ConfigFactory.load().getConfig("maquette.adapters.common-settings.fs");
      var config = FileSystemRepositoryConfiguration.apply(cfg);

      return DockerInfrastructureProvider.apply(config.getDirectory(), om);
   }

}
