package maquette.core.common.validation.validators;

import lombok.AllArgsConstructor;
import maquette.core.common.validation.api.ValidationContext;
import maquette.core.common.validation.api.Validator;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor(staticName = "apply")
public final class NonEmptyListValidator<T> implements Validator<List<T>> {

    @Override
    public boolean validate(ValidationContext context, String fieldName, List<T> values) throws Exception {
        if (Objects.isNull(values) || values.isEmpty()) {
            context.addErrorMessage("`%s` must not be empty.", fieldName);
        }

        return true;
    }

}
