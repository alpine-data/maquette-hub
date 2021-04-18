package maquette.core.services.users;

import maquette.core.config.RuntimeConfiguration;
import maquette.core.services.data.DataAssetCompanion;

public final class UserServicesFactory {

   private UserServicesFactory() {

   }

   public static UserServices apply(RuntimeConfiguration runtime) {
      var comp = UserCompanion.apply(runtime.getUsers());
      var dataAssetCompanion = DataAssetCompanion.apply(runtime);

      return UserServicesImpl.apply(
         runtime.getDataAssets(), runtime.getProjects(), runtime.getUsers(), comp, dataAssetCompanion);
   }

}
