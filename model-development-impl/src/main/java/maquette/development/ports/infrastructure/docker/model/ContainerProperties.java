package maquette.development.ports.infrastructure.docker.model;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.net.URL;
import java.util.Map;

@Value
@AllArgsConstructor(staticName = "apply")
public class ContainerProperties {

   ContainerConfig config;

   ContainerStatus status;

   Map<Integer, URL> mappedPortUrls;

}
