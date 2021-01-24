package maquette.streams.core.entities.topic.exceptions;

import maquette.streams.common.DomainException;

public final class SchemaDoesNotMatchException extends RuntimeException implements DomainException {

   private SchemaDoesNotMatchException(String message) {
      super(message);
   }

   public static SchemaDoesNotMatchException apply(String topic) {
      String message = "The schema of the records does not match the schema of topic `" + topic + "`";
      return new SchemaDoesNotMatchException(message);
   }

}
