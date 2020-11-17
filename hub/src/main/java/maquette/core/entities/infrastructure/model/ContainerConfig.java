package maquette.core.entities.infrastructure.model;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Value
@AllArgsConstructor(staticName = "apply")
public class ContainerConfig {

    String name;

    String image;

    String command;

    Map<String, String> environment;

    List<PortSpec> ports;

    String memory;

    Double cores;

    public static ContainerConfigBuilder builder(String name, String image) {
        return ContainerConfigBuilder.apply(name, image);
    }

    public Map<String, String> getEnvironment() {
        if (environment == null) {
            return Maps.newHashMap();
        } else {
            return Map.copyOf(environment);
        }
    }

    public Optional<String> getCommand() {
        return Optional.ofNullable(command);
    }

    public List<PortSpec> getPorts() {
        if (ports == null) {
            return List.of();
        } else {
            return List.copyOf(ports);
        }
    }

    public Optional<String> getMemory() {
        return Optional.ofNullable(memory);
    }

    public Optional<Double> getCores() {
        return Optional.ofNullable(cores);
    }

}
