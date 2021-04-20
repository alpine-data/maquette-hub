package maquette.core.services.sandboxes;

import maquette.core.config.RuntimeConfiguration;
import maquette.core.services.projects.ProjectCompanion;

public final class SandboxServicesFactory {

   private SandboxServicesFactory() {

   }

   public static SandboxServices apply(RuntimeConfiguration runtime) {
      var sandboxCompanion = SandboxCompanion.apply(runtime.getProcessManager(), runtime.getInfrastructureManager());
      var projectCompanion = ProjectCompanion.apply(runtime.getProjects(), runtime.getInfrastructureManager());
      var impl = SandboxServicesImpl.apply(
         runtime.getProcessManager(), runtime.getInfrastructureManager(), runtime.getProjects(),
         runtime.getSandboxes(),  runtime.getUsers(),projectCompanion, sandboxCompanion);

      return SandboxServicesSecured.apply(impl, projectCompanion);
   }

}
