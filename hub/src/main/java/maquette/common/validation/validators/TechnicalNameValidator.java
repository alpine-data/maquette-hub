package maquette.common.validation.validators;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.common.validation.api.ValidationContext;
import maquette.common.validation.api.Validator;

@AllArgsConstructor(staticName = "apply", access = AccessLevel.PRIVATE)
public final class TechnicalNameValidator implements Validator<String> {

   private final RegExStringValidator delegate;

   public static TechnicalNameValidator apply() {
      return apply(RegExStringValidator.apply("[a-z][a-z0-9-]{2,}"));
   }

   @Override
   public boolean validate(ValidationContext context, String fieldName, String value) throws Exception {
      return delegate.validate(context, fieldName, value);
   }

   @Override
   public void onException(ValidationContext context, String fieldName, String value, Exception ex) {
      delegate.onException(context, fieldName, value, ex);
   }

}
