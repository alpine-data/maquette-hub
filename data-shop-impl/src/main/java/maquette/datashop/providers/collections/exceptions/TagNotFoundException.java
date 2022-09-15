package maquette.datashop.providers.collections.exceptions;

public final class TagNotFoundException extends RuntimeException {

    private TagNotFoundException(String message) {
        super(message);
    }

    public static TagNotFoundException withName(String tag) {
        var msg = String.format("Tag `%s` does not exist.", tag);
        return new TagNotFoundException(msg);
    }

}
