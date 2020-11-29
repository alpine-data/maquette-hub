package maquette.core.entities.sandboxes.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.sandboxes.model.stacks.DeployedStackProperties;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import org.glassfish.jersey.internal.guava.Sets;

import java.util.Set;
import java.util.stream.Collectors;

@Value
@AllArgsConstructor(staticName = "apply")
public class SandboxProperties {

   UID id;

   String name;

   ActionMetadata created;

   Set<DeployedStackProperties> stacks;

   Set<Integer> processes;

   private SandboxProperties() {
      this(null, null, null, Sets.newHashSet(), Sets.newHashSet());
   }

   public static SandboxProperties apply(UID id, String name, ActionMetadata created) {
      return apply(id, name, created, Sets.newHashSet(), Sets.newHashSet());
   }

   public SandboxProperties withDeployment(DeployedStackProperties deployment) {
      var deployments = Sets.<DeployedStackProperties>newHashSet();
      deployments.addAll(this.stacks);
      deployments.add(deployment);

      return SandboxProperties.apply(id, name, created, deployments, processes);
   }

   public SandboxProperties removeDeployment(String deploymentId) {
      var deployments = this.stacks
         .stream()
         .filter(s -> !s.getDeployment().equals(deploymentId))
         .collect(Collectors.toSet());

      return SandboxProperties.apply(id, name, created, deployments, processes);
   }

   public SandboxProperties withProcess(int pid) {
      var processes = Sets.<Integer>newHashSet();
      processes.addAll(this.processes);
      processes.add(pid);

      return SandboxProperties.apply(id, name, created, stacks, processes);
   }

}
