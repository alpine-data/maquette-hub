package maquette.core.common.validation.validators;

import akka.japi.Function;
import akka.japi.Pair;
import akka.japi.function.Function3;
import akka.japi.function.Procedure4;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.common.validation.api.ValidationContext;
import maquette.core.common.validation.api.Validator;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor(staticName = "apply", access = AccessLevel.PRIVATE)
public final class ObjectValidator<T> implements Validator<T> {

    private final List<Pair<Function3<ValidationContext, String, T, Boolean>, Procedure4<ValidationContext, String, T
        , Exception>>> checks;

    private final boolean required;

    public static <T> Builder<T> build() {
        return Builder.apply(Lists.newArrayList());
    }

    @Override
    public boolean validate(ValidationContext context, String fieldName, T value) {
        if (required && Objects.isNull(value)) {
            context.addErrorMessage("`%s` must be specified");
        }

        if (!Objects.isNull(value)) {
            var cont = true;
            for (var check : checks) {
                if (cont) {
                    try {
                        cont = check.first().apply(context, fieldName, value);
                    } catch (Exception e) {
                        Operators.suppressExceptions(() -> check.second().apply(context, fieldName, value, e));
                    }
                }
            }
        }

        return true;
    }

    public ObjectValidator<T> optional() {
        return apply(checks, false);
    }

    public ObjectValidator<T> required() {
        return apply(checks, true);
    }

    @AllArgsConstructor(staticName = "apply", access = AccessLevel.PRIVATE)
    public static class Builder<T> {

        private final List<Pair<Function3<ValidationContext, String, T, Boolean>, Procedure4<ValidationContext,
            String, T, Exception>>> checks;

        /**
         * Add validation for a field/ value pair.
         *
         * @param fieldName The field to be checked.
         * @param value     The value to be checked.
         * @param validator The validator which can be called to validate the value.
         * @param <R>       The type of the validated field.
         * @return This instance of the validation
         */
        public <R> Builder<T> validate(String fieldName, Function<T, R> value, Validator<R> validator) {
            var pair = Pair.<Function3<ValidationContext, String, T, Boolean>, Procedure4<ValidationContext, String,
                T, Exception>>apply(
                (ctx, prefix, obj) -> validator.validate(ctx, String.format("%s.%s", prefix, fieldName),
                    value.apply(obj)),
                (ctx, prefix, obj, ex) -> validator.onException(ctx, String.format("%s.%s", prefix, fieldName),
                    value.apply(obj), ex));

            checks.add(pair);
            return this;
        }

        public ObjectValidator<T> required() {
            return ObjectValidator.apply(checks, true);
        }

        public ObjectValidator<T> optional() {
            return ObjectValidator.apply(checks, false);
        }

    }

}
