package maquette.streams.core.entities.topic.exceptions;

import maquette.streams.common.DomainException;

public final class TopicDoesNotExist extends RuntimeException implements DomainException {

   private TopicDoesNotExist(String message) {
      super(message);
   }

   public static TopicDoesNotExist apply(String topic) {
      String message = "The topic `\" + topic + \"` does not exist.";
      return new TopicDoesNotExist(message);
   }

}
