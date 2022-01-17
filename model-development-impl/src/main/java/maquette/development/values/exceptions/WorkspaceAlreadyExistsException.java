package maquette.development.values.exceptions;

import maquette.core.common.exceptions.ApplicationException;

public final class WorkspaceAlreadyExistsException extends ApplicationException {

   private WorkspaceAlreadyExistsException(String message) {
      super(message);
   }

   public static WorkspaceAlreadyExistsException apply(String project) {
      var msg = String.format("A workspace with the name `%s` already exists.", project);
      return new WorkspaceAlreadyExistsException(msg);
   }

}
