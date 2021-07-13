package maquette.core.common.validation.validators;

import lombok.AllArgsConstructor;
import maquette.core.common.validation.api.ValidationContext;
import maquette.core.common.validation.api.Validator;

import java.util.Objects;

@AllArgsConstructor(staticName = "apply")
public final class NotNullValidator implements Validator<Object> {

   private final boolean cont;

   public static NotNullValidator apply() {
      return apply(true);
   }

   @Override
   public boolean validate(ValidationContext context, String fieldName, Object value) {
      if (Objects.isNull(value)) {
         context.addErrorMessage("`%s` must be set.");
      }

      return cont;
   }

}
