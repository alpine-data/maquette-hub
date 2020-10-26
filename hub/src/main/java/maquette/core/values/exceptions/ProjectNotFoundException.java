package maquette.core.values.exceptions;

public class ProjectNotFoundException extends MaquetteUserException {

   private ProjectNotFoundException(String message) {
      super(message);
   }

   public static ProjectNotFoundException applyFromName(String name) {
      String msg = String.format("Project with name `%s` was not found.", name);
      return new ProjectNotFoundException(msg);
   }

   public static ProjectNotFoundException applyFromId(String id) {
      String msg = String.format("Project with id `%s` was not found.", id);
      return new ProjectNotFoundException(msg);
   }

}
