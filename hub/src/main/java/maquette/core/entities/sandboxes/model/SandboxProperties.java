package maquette.core.entities.sandboxes.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.glassfish.jersey.internal.guava.Sets;

import java.util.Set;
import java.util.stream.Collectors;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class SandboxProperties {

   String id;

   String name;

   Set<String> deployments;

   Set<Integer> processes;

   public static SandboxProperties apply(String id, String name) {
      return apply(id, name, Sets.newHashSet(), Sets.newHashSet());
   }

   public SandboxProperties withDeployment(String deploymentId) {
      var deployments = Sets.<String>newHashSet();
      deployments.addAll(this.deployments);
      deployments.add(deploymentId);

      return SandboxProperties.apply(id, name, deployments, processes);
   }

   public SandboxProperties removeDeployment(String deploymentId) {
      var deployments = this.deployments
         .stream()
         .filter(s -> !s.equals(deploymentId))
         .collect(Collectors.toSet());

      return SandboxProperties.apply(id, name, deployments, processes);
   }

   public SandboxProperties withProcess(int pid) {
      var processes = Sets.<Integer>newHashSet();
      processes.addAll(this.processes);
      processes.add(pid);

      return SandboxProperties.apply(id, name, deployments, processes);
   }

}
