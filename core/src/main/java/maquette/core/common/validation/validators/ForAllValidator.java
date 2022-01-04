package maquette.core.common.validation.validators;

import lombok.AllArgsConstructor;
import maquette.core.common.validation.api.ValidationContext;
import maquette.core.common.validation.api.Validator;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor(staticName = "apply")
public final class ForAllValidator<T> implements Validator<List<T>> {

    List<Validator<? super T>> validators;

    @SafeVarargs
    public static <T> ForAllValidator<T> apply(Validator<? super T> ...validations) {
        return apply(Arrays.asList(validations));
    }

    @Override
    public boolean validate(ValidationContext context, String fieldName, List<T> values) throws Exception {
        for (var value : values) {
            for (var validator : validators) {
                var p = validator.validate(context, fieldName, value);

                if (!p) {
                    return false;
                }
            }
        }

        return true;
    }

}
