package maquette.core.services.dependencies;

import maquette.core.config.RuntimeConfiguration;

public final class DependencyServicesFactory {

   private DependencyServicesFactory() {

   }

   public static DependencyServices apply(RuntimeConfiguration runtime) {
      var companion = DependencyCompanion.apply(runtime);
      return DependencyServicesImpl.apply(
         companion, runtime.getDependencies(), runtime.getDataAssets(),
         runtime.getProjects(), runtime.getUsers());
   }

}
