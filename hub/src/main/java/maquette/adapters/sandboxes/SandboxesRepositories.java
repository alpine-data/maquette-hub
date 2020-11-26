package maquette.adapters.sandboxes;

import com.fasterxml.jackson.databind.ObjectMapper;
import maquette.core.ports.SandboxesRepository;
import org.apache.commons.lang.NotImplementedException;

public final class SandboxesRepositories {

   private SandboxesRepositories() {

   }

   public static SandboxesRepository create(ObjectMapper om) {
      var config = SandboxesRepositoryConfiguration.apply();

      switch (config.getType()) {
         case "filesystem":
         case "fs":
         case "files":
            return FileSystemSandboxesRepository.apply(config.getFs(), om);

         default:
         case "in-mem":
         case "in-memory":
            throw new NotImplementedException();
      }
   }

}