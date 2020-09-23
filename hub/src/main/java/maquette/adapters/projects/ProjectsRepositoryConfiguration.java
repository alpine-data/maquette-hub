package maquette.adapters.projects;

import com.typesafe.config.ConfigFactory;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class ProjectsRepositoryConfiguration {

   String type;

   FileSystemProjectsRepositoryConfiguration fs;

   public static ProjectsRepositoryConfiguration apply() {
      var config = ConfigFactory.load().getConfig("maquette.adapters.projects-repository");
      var fsConfig = FileSystemProjectsRepositoryConfiguration.apply(config.getConfig("fs"));
      var type = config.getString("type");

      return apply(type, fsConfig);
   }

}
