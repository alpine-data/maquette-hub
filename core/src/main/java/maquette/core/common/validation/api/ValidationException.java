package maquette.core.common.validation.api;

import com.google.common.collect.Lists;
import maquette.core.common.exceptions.ApplicationException;

import java.util.List;

public final class ValidationException extends ApplicationException {

    private final List<String> messages;

    public ValidationException(String message) {
        super(message);
        this.messages = Lists.newArrayList();
    }

    public ValidationException(String message, List<String> messages) {
        super(message);
        this.messages = messages;
    }

    public static ValidationException apply(String message) {
        return new ValidationException(message);
    }

    public static ValidationException apply(String message, List<String> messages) {
        return new ValidationException(message, messages);
    }

    public List<String> getMessages() {
        return messages;
    }

}
