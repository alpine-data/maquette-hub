package maquette.core.entities.projects.exceptions;

import maquette.core.values.exceptions.DomainException;

public class ProjectAlreadyExistsException extends RuntimeException implements DomainException {

   private ProjectAlreadyExistsException(String message) {
      super(message);
   }

   public static ProjectAlreadyExistsException apply(String project) {
      var msg = String.format("A project with the name `%s` already exists.", project);
      return new ProjectAlreadyExistsException(msg);
   }

}
