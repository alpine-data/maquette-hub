package maquette.core.values.binary;

import maquette.core.common.Operators;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class BinaryObjects {

    public static CompressedBinaryObject compress(Path file) {
        return CompressedBinaryObject.fromFile(file);
    }

    public static BinaryObject fromBytes(byte[] bytes) {
        return ByteArrayBinaryObject.apply(bytes);
    }

    public static BinaryObject fromInputStream(InputStream is) {
        return Operators.suppressExceptions(() -> {
            var buffer = new byte[1024];
            var tmp = Files.createTempFile("mq", "binary");

            try (var os = Files.newOutputStream(tmp)) {
                int len;

                while ((len = is.read(buffer)) > 0) {
                    os.write(buffer, 0, len);
                }
            }

            return fromTemporaryFile(tmp);
        });
    }

    public static CompressedBinaryObject fromDirectory(Path file) {
        return CompressedBinaryObject.fromDirectory(file);
    }

    public static BinaryObject fromFile(Path file) {
        return FileBinaryObject.apply(file, false);
    }

    public static BinaryObject fromTemporaryFile(Path file) {
        return FileBinaryObject.apply(file, true);
    }

    public static BinaryObject empty() {
        return ByteArrayBinaryObject.apply(new byte[]{});
    }

}
