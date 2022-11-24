package maquette.development.configuration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import maquette.core.config.annotations.ConfigurationProperties;
import maquette.core.config.annotations.Value;

import java.util.Objects;

@Getter
@ConfigurationProperties
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@AllArgsConstructor(staticName = "apply")
public class PythonStackConfiguration {

    @Value("memory-request-s")
    PythonStackMemoryRequest memoryRequestS;

    @Value("memory-request-m")
    PythonStackMemoryRequest memoryRequestM;

    public double getPriceByMemoryRequest(String size) throws RuntimeException {
        if (Objects.equals(size, memoryRequestS.memoryRequest)) {
            return this.memoryRequestS.price;
        } else if (Objects.equals(size, memoryRequestM.memoryRequest)) {
            return this.memoryRequestM.price;
        } else {
            throw new RuntimeException("Not yet implemented");
        }
    }

    @Getter
    @ConfigurationProperties
    @NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
    @AllArgsConstructor(staticName = "apply")
    public static class PythonStackMemoryRequest {

        @Value("price")
        double price;

        @Value("memory-request")
        String memoryRequest;

        @Value("memory-request-string")
        String memoryRequestString;
    }
}
