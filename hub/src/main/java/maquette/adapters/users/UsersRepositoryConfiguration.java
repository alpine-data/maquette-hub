package maquette.adapters.users;

import com.typesafe.config.ConfigFactory;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class UsersRepositoryConfiguration {

   String type;

   FileSystemUsersRepositoryConfiguration fs;

   public static UsersRepositoryConfiguration apply() {
      var config = ConfigFactory.load().getConfig("maquette.adapters.users-repository");
      var fsConfig = FileSystemUsersRepositoryConfiguration.apply(config.getConfig("fs"));
      var type = config.getString("type");

      return apply(type, fsConfig);
   }

}
