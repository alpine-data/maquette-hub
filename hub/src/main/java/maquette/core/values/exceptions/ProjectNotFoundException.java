package maquette.core.values.exceptions;

import maquette.core.values.UID;

public class ProjectNotFoundException extends RuntimeException implements DomainException {

   private ProjectNotFoundException(String message) {
      super(message);
   }

   public static ProjectNotFoundException applyFromName(String name) {
      String msg = String.format("Project with name `%s` was not found.", name);
      return new ProjectNotFoundException(msg);
   }

   public static ProjectNotFoundException applyFromId(UID id) {
      String msg = String.format("Project with id `%s` was not found.", id);
      return new ProjectNotFoundException(msg);
   }

}
