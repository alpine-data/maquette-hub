package maquette.core.values.binary;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@With
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "apply")
public class FileSize {

    private static final long ONE_BYTE = 1;
    private static final long ONE_KB = 1024;
    private static final long ONE_MB = ONE_KB * ONE_KB;
    private static final long ONE_GB = ONE_MB * ONE_KB;
    private static final long ONE_TB = ONE_GB * ONE_KB;

    long size;
    Unit unit;

    public static FileSize empty() {
        return apply(0, Unit.BYTES);
    }

    @JsonIgnore
    public long getBytes() {
        return getSize() * getUnit().getFactor();
    }

    public enum Unit {
        BYTES(ONE_BYTE, "Bytes"),
        KILOBYTES(ONE_KB, "KB"),
        MEGABYTES(ONE_MB, "MB"),
        GIGABYTES(ONE_GB, "GB"),
        TERABYTES(ONE_TB, "TB");

        private final long factor;
        private final String name;

        Unit(long factor, String name) {
            this.factor = factor;
            this.name = name;
        }

        public long getFactor() {
            return factor;
        }

        public String getName() {
            return name;
        }
    }

    public FileSize add(FileSize other) {
        return FileSize.apply(this.getBytes() + other.getBytes(), Unit.BYTES);
    }

    @JsonProperty("humanized")
    public String toSizeAdaptedString() {
        Unit unit = Unit.BYTES;
        double size = getBytes();

        double bytes = getBytes();

        if (bytes > ONE_TB) {
            unit = Unit.TERABYTES;
            size = bytes / ONE_TB;
        } else if (bytes > ONE_GB) {
            unit = Unit.GIGABYTES;
            size = bytes / ONE_GB;
        } else if (bytes > ONE_MB) {
            unit = Unit.MEGABYTES;
            size = bytes / ONE_MB;
        } else if (bytes > ONE_KB) {
            unit = Unit.KILOBYTES;
            size = bytes / ONE_KB;
        }


        return String.format("%.2f %s", size, unit.name);

    }

}

