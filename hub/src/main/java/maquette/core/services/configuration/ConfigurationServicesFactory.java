package maquette.core.services.configuration;

import maquette.core.entities.users.UserEntities;

public final class ConfigurationServicesFactory {

   private ConfigurationServicesFactory() {

   }

   public static ConfigurationServices apply(UserEntities users) {
      return ConfigurationServicesImpl.apply(users);
   }

}
