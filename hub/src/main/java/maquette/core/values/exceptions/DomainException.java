package maquette.core.values.exceptions;

public interface DomainException {

   String getMessage();

   default int getStatus() {
      return 400;
   }

}
