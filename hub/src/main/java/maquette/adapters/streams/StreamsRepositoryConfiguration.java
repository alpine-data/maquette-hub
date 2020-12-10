package maquette.adapters.streams;

import com.typesafe.config.ConfigFactory;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class StreamsRepositoryConfiguration {

   String type;

   FileSystemStreamsRepositoryConfiguration fs;

   public static StreamsRepositoryConfiguration apply() {
      var config = ConfigFactory.load().getConfig("maquette.adapters.streams-repository");
      var type = config.getString("type");
      var fsConfig = FileSystemStreamsRepositoryConfiguration.apply(config.getConfig("fs"));
      return apply(type, fsConfig);
   }

}
