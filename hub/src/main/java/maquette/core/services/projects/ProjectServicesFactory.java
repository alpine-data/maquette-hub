package maquette.core.services.projects;

import maquette.core.config.RuntimeConfiguration;
import maquette.core.services.data.DataAssetCompanion;
import maquette.core.services.sandboxes.SandboxCompanion;

public final class ProjectServicesFactory {

   private ProjectServicesFactory() {

   }

   public static ProjectServices apply(RuntimeConfiguration runtime) {
      var comp = ProjectCompanion.apply(runtime.getProjects(), runtime.getInfrastructureManager());
      var sandboxCompanion = SandboxCompanion.apply(runtime.getProcessManager(), runtime.getInfrastructureManager());
      var assetCompanion = DataAssetCompanion.apply(runtime);
      var projectCompanion = ProjectCompanion.apply(runtime.getProjects(), runtime.getInfrastructureManager());

      var impl = ProjectServicesImpl.apply(
         runtime.getProcessManager(), runtime.getProjects(), runtime.getSandboxes(), runtime.getInfrastructureManager(),
         runtime.getDataAssets(), assetCompanion, projectCompanion, sandboxCompanion, runtime.getGitClient());

      var cached = ProjectServicesCached.apply(impl);

      return ProjectServicesSecured.apply(cached, comp);
   }

}
