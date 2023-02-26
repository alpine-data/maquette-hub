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
public class PythonGPUStackConfiguration {

    @Value("size-s")
    PythonGPUStackSize sizeS;

    @Value("size-m")
    PythonGPUStackSize sizeM;

    @Value("size-l")
    PythonGPUStackSize sizeL;

    public double getPriceBySize(String size) throws RuntimeException {
        if (Objects.equals(size, sizeS.size)) {
            return this.sizeS.price;
        } else if (Objects.equals(size, sizeM.size)) {
            return this.sizeM.price;
        } else if (Objects.equals(size, sizeL.size)) {
            return this.sizeL.price;
        } else {
            return 0.0;
        }
    }

    @Getter
    @ConfigurationProperties
    @NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
    @AllArgsConstructor(staticName = "apply")
    public static class PythonGPUStackSize {

        @Value("price")
        double price;

        @Value("size")
        String size;

        @Value("size-string")
        String sizeString;
    }
}
