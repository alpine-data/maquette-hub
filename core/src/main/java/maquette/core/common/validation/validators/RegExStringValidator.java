package maquette.core.common.validation.validators;

import lombok.AllArgsConstructor;
import maquette.core.common.validation.api.ValidationContext;
import maquette.core.common.validation.api.Validator;

import java.util.Objects;
import java.util.regex.Pattern;

@AllArgsConstructor(staticName = "apply")
public final class RegExStringValidator implements Validator<String> {

   private final Pattern regex;

   private final String pattern;

   public static RegExStringValidator apply(String regex) {
      return apply(Pattern.compile(regex), regex);
   }

   @Override
   public boolean validate(ValidationContext context, String fieldName, String value) {
      if (Objects.isNull(value) || !regex.matcher(value).matches()) {
         context.addErrorMessage("`%s` is not valid. The value must match the following regex `%s`.", fieldName, pattern);
      }

      return true;
   }

}
