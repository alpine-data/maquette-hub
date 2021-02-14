package maquette.core.services.configuration;

import lombok.AllArgsConstructor;
import maquette.core.entities.users.UserEntities;
import maquette.core.entities.users.model.UserProfile;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class ConfigurationServicesImpl implements ConfigurationServices {

   private final UserEntities users;

   @Override
   public CompletionStage<String> getDefaultDataOwner() {
      return users.getUsers().thenApply(users -> users
         .stream()
         .map(UserProfile::getId)
         .sorted()
         .findFirst()
         .orElse("john"));
   }

}
