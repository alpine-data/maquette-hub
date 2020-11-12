package maquette.core.entities.datasets.exceptions;

public final class InvalidAvroFileException extends RuntimeException {

    private InvalidAvroFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public static InvalidAvroFileException apply(Throwable cause) {
        String message = "The provided file cannot be read. Is it a valid avro file?";

        return new InvalidAvroFileException(message, cause);
    }

}
