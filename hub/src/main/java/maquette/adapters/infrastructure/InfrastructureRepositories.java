package maquette.adapters.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.ConfigFactory;
import maquette.core.ports.InfrastructureRepository;

public final class InfrastructureRepositories {

   private InfrastructureRepositories() {

   }

   public static InfrastructureRepository create(ObjectMapper om) {
      var config = InfrastructureRepositoryConfiguration.apply();

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
