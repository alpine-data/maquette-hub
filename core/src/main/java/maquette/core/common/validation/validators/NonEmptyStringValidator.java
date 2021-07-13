package maquette.core.common.validation.validators;

import lombok.AllArgsConstructor;
import maquette.core.common.validation.api.ValidationContext;
import maquette.core.common.validation.api.Validator;

import java.util.Objects;

@AllArgsConstructor(staticName = "apply")
public final class NonEmptyStringValidator implements Validator<String> {

   private final int minLength;

   public static NonEmptyStringValidator apply() {
      return apply(1);
   }

   @Override
   public boolean validate(ValidationContext context, String fieldName, String value) throws Exception {
      if (Objects.isNull(value) || value.length() < minLength) {
         context.addErrorMessage("`%s` must be set with at least %d characters", fieldName, minLength);
      }

      return true;
   }
}
