package maquette.adapters.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import maquette.config.RepositoryConfiguration;
import maquette.core.entities.infrastructure.ports.InfrastructureRepository;

public final class InfrastructureRepositories {

   private InfrastructureRepositories() {

   }

   public static InfrastructureRepository create(ObjectMapper om) {
      var config = RepositoryConfiguration.apply("infrastructure-repository");

      switch (config.getType()) {
         case "filesystem":
         case "fs":
         case "files":
            return FileSystemInfrastructureRepository.apply(config.getFs(), om);

         default:
         case "in-mem":
         case "in-memory":
            return InMemoryInfrastructureRepository.apply();
      }
   }

}
