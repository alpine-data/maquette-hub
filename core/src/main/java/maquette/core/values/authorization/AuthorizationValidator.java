package maquette.core.values.authorization;

import lombok.AllArgsConstructor;
import maquette.core.common.validation.api.ValidationContext;
import maquette.core.common.validation.api.Validator;
import maquette.core.common.validation.validators.NonEmptyStringValidator;
import maquette.core.common.validation.validators.ObjectValidator;

import java.util.Objects;

@AllArgsConstructor(staticName = "apply")
public final class AuthorizationValidator implements Validator<Authorization> {

   private final Class<? extends Authorization> authType;

   private final boolean required;

   private static final ObjectValidator<UserAuthorization> USER_AUTHORIZATION_VALIDATOR = ObjectValidator
      .<UserAuthorization>build()
      .validate("name", UserAuthorization::getName, NonEmptyStringValidator.apply())
      .required();

   private static final ObjectValidator<RoleAuthorization> ROLE_AUTHORIZATION_VALIDATOR = ObjectValidator
      .<RoleAuthorization>build()
      .validate("name", RoleAuthorization::getName, NonEmptyStringValidator.apply())
      .required();

   public static AuthorizationValidator apply() {
      return apply(null, true);
   }

   public static AuthorizationValidator apply(Class<? extends Authorization> authType) {
      return apply(authType, true);
   }

   @Override
   public boolean validate(ValidationContext context, String fieldName, Authorization value) {
      if (required && Objects.isNull(value)) {
         context.addErrorMessage("`%s` must be set");
      }

      if (!Objects.isNull(authType) && !authType.isInstance(value)) {
         context.addErrorMessage("authorization type is not allowed for field `%s`");
      }

      if (!Objects.isNull(value)) {
         if (value instanceof UserAuthorization) {
            var auth = (UserAuthorization) value;
            return USER_AUTHORIZATION_VALIDATOR.validate(context, fieldName, auth);
         } else if (value instanceof RoleAuthorization) {
            var auth = (RoleAuthorization) value;
            return ROLE_AUTHORIZATION_VALIDATOR.validate(context, fieldName, auth);
         }
      }

      return false;
   }

}
