package maquette.adapters.streams;

import com.fasterxml.jackson.databind.ObjectMapper;
import maquette.config.RepositoryConfiguration;
import maquette.core.ports.StreamsRepository;
import org.apache.commons.lang.NotImplementedException;

public class StreamsRepositories {

   private StreamsRepositories() {

   }

   public static StreamsRepository create(ObjectMapper om) {
      var config = RepositoryConfiguration.apply("streams-repository");

      switch (config.getType()) {
         case "filesystem":
         case "fs":
         case "files":
            return FileSystemStreamsRepository.apply(config.getFs(), om);

         default:
         case "in-mem":
         case "in-memory":
            throw new NotImplementedException();
      }
   }

}
