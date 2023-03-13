package maquette.core.common.validation.validators;

import lombok.AllArgsConstructor;
import maquette.core.common.validation.api.ValidationContext;
import maquette.core.common.validation.api.Validator;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor(staticName = "apply")
public final class EnumStringValidator implements Validator<String> {

    private final List<String> valid;

    public static EnumStringValidator apply() {
        return apply(List.of());
    }

    @Override
    public boolean validate(ValidationContext context, String fieldName, String value) throws Exception {
        if (Objects.isNull(value) || !valid.contains(value)) {
            context.addErrorMessage("`%s` must be one of the following: %s", fieldName, String.join(", ", valid));
        }

        return true;
    }
}
