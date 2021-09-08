package maquette.core.common.validation.api;

import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(staticName = "apply", access = AccessLevel.PRIVATE)
public final class ValidationContext {

    private final List<String> errors;

    public static ValidationContext apply() {
        return apply(Lists.newArrayList());
    }

    public void addErrorMessage(String message, Object... params) {
        this.errors.add(String.format(message, params));
    }

}
