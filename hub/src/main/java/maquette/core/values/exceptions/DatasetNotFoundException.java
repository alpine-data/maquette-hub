package maquette.core.values.exceptions;

public class DatasetNotFoundException extends MaquetteUserException {

   private DatasetNotFoundException(String message) {
      super(message);
   }

   public static DatasetNotFoundException applyFromName(String projectName, String datasetName) {
      String msg = String.format("Dataset with name `%s` was not found in project `%s`.", datasetName, projectName);
      return new DatasetNotFoundException(msg);
   }

   public static DatasetNotFoundException applyFromId(String projectId, String datasetId) {
      String msg = String.format("Dataset with id `%s` was not found in project `%s`.", datasetId, projectId);
      return new DatasetNotFoundException(msg);
   }

}
