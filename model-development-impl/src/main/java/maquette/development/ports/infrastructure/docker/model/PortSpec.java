package maquette.development.ports.infrastructure.docker.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Optional;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PortSpec {

    private static final String CONTAINER_PORT = "containerPort";
    private static final String HOST_PORT = "hostPort";

    @JsonProperty(CONTAINER_PORT)
    Integer containerPort;

    @JsonProperty(HOST_PORT)
    Integer hostPort;

    @JsonCreator
    public static PortSpec apply(
        @JsonProperty(CONTAINER_PORT) Integer containerPort,
        @JsonProperty(HOST_PORT) Integer hostPort) {

        return new PortSpec(containerPort, hostPort);
    }

    public static PortSpec apply(int containerPort) {
        return apply(containerPort, null);
    }

    public Optional<Integer> getHostPort() {
        return Optional.ofNullable(hostPort);
    }

}
