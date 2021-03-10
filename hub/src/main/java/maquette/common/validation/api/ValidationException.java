package maquette.common.validation.api;

import lombok.AllArgsConstructor;
import maquette.core.values.exceptions.DomainException;

import java.util.List;

@AllArgsConstructor(staticName = "apply")
public final class ValidationException extends RuntimeException implements DomainException {

   private final String message;

   private final List<String> messages;

   @Override
   public String getMessage() {
      return message;
   }

   public List<String> getMessages() {
      return messages;
   }

}
