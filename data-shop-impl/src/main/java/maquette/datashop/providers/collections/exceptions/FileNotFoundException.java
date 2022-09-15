package maquette.datashop.providers.collections.exceptions;


public final class FileNotFoundException extends RuntimeException {

    private FileNotFoundException(String message) {
        super(message);
    }

    public static FileNotFoundException withName(String name) {
        var msg = String.format("File `%s` does not exist.", name);
        return new FileNotFoundException(msg);
    }

}
