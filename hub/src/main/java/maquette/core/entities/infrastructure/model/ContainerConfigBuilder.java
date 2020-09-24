package maquette.core.entities.infrastructure.model;

import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ContainerConfigBuilder {

   private final String name;

   private final String image;

   private String command;

   private final Map<String, String> environment;

   private final List<PortSpec> ports;

   public static ContainerConfigBuilder apply(String name, String image) {
      return new ContainerConfigBuilder(name, image, null, Maps.newHashMap(), Lists.newArrayList());
   }

   public ContainerConfigBuilder withCommand(String command) {
      this.command = command;
      return this;
   }

   public ContainerConfigBuilder withEnvironmentVariable(String key, String value) {
      this.environment.put(key, value);
      return this;
   }

   public ContainerConfigBuilder withPort(int containerPort) {
      ports.add(PortSpec.apply(containerPort));
      return this;
   }

   public ContainerConfigBuilder withPort(int containerPort, int hostPort) {
      ports.add(PortSpec.apply(containerPort, hostPort));
      return this;
   }

   public ContainerConfig build() {
      return ContainerConfig.apply(name, image, command, environment, ports);
   }

}
