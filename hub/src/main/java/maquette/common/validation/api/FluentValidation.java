package maquette.common.validation.api;

import akka.Done;
import akka.japi.Function;
import akka.japi.Pair;
import akka.japi.function.Procedure2;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.common.Operators;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Utility class to validate user input.
 */
@AllArgsConstructor(staticName = "apply", access = AccessLevel.PRIVATE)
public final class FluentValidation {

   private final List<Pair<Function<ValidationContext, Boolean>, Procedure2<ValidationContext, Exception>>> checks;

   private final String errorMessage;

   /**
    * Creates a new instance.
    *
    * @return A new instance
    */
   public static FluentValidation apply() {
      return apply(Lists.newArrayList(), "Validation of user input failed");
   }

   /**
    * Add validation for a field/ value pair.
    *
    * @param fieldName The field to be checked.
    * @param value     The value to be checked.
    * @param validator The validator which can be called to validate the value.
    * @param <T>       The type of the validated field.
    * @return This instance of the validation
    */
   public <T> FluentValidation validate(String fieldName, T value, Validator<T> validator) {
      var pair = Pair.<Function<ValidationContext, Boolean>, Procedure2<ValidationContext, Exception>>apply(
         ctx -> validator.validate(ctx, fieldName, value),
         (ctx, ex) -> validator.onException(ctx, fieldName, value, ex));

      checks.add(pair);
      return this;
   }

   /**
    * Set the default error message if the validation fails.
    *
    * @param errorMessage The error message
    * @return A new validation instance
    */
   public FluentValidation withErrorMessage(String errorMessage) {
      return apply(checks, errorMessage);
   }

   /**
    * Run validations and throw exception if validations failed.
    */
   public void checkAndThrow() {
      var context = runChecks();

      if (!context.getErrors().isEmpty()) {
         throw ValidationException.apply(errorMessage, context.getErrors());
      }
   }

   /**
    * Run validations and return Future (failed or successful).
    *
    * @return A future.
    */
   public CompletionStage<Done> checkAndFail() {
      var context = runChecks();

      if (!context.getErrors().isEmpty()) {
         return CompletableFuture.failedFuture(ValidationException.apply(errorMessage, context.getErrors()));
      } else {
         return CompletableFuture.completedFuture(Done.getInstance());
      }
   }

   private ValidationContext runChecks() {
      var context = ValidationContext.apply();
      var cont = true;
      for (var check : checks) {
         if (cont) {
            try {
               cont = check.first().apply(context);
            } catch (Exception e) {
               Operators.suppressExceptions(() -> check.second().apply(context, e));
            }
         }
      }

      return context;
   }

}
