package maquette.core.entities.projects.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.core.values.UID;

@Value
@AllArgsConstructor(staticName = "apply")
public class MlflowConfiguration {

   String deploymentName;

   String minioAccessKey;

   String minioSecretKey;

   String postgresPassword;

   String postgresUsername;

   String internalTrackingUrl;

   @SuppressWarnings("unused")
   private MlflowConfiguration() {
      this.deploymentName = "";
      this.minioAccessKey = "";
      this.minioSecretKey = "";
      this.postgresPassword = "";
      this.postgresUsername = "";
      this.internalTrackingUrl = "";
   }

   public static MlflowConfiguration apply(UID project) {
      var deploymentId = String.format("mq--%s--mlflow", project);
      var minioAccessKey = Operators.randomHash();
      var minioSecretKey = Operators.randomHash();
      var postgresPassword = Operators.randomHash();
      var postgresUsername = Operators.randomHash();

      return apply(deploymentId, minioAccessKey, minioSecretKey, postgresPassword, postgresUsername, null);
   }

   public String getMinioContainerName(UID project) {
      return String.format("mq--%s--minio", project);
   }

   public String getMlflowBasePath(UID project) {
      return String.format("/_mlflow/%s", project);
   }

   public String getMlflowContainerName(UID project) {
      return String.format("mq--%s--mlflow", project);
   }

   public String getPostgreContainerName(UID project) {
      return String.format("mq--%s--postgres", project);
   }

   public String getSandboxNetworkName(UID project) {
      return String.format("mq--%s--sandboxes", project);
   }

   public MlflowConfiguration withTrackingUrl(String internalTrackingUrl) {
      return apply(
         deploymentName, minioAccessKey, minioSecretKey,
         postgresPassword, postgresUsername, internalTrackingUrl);
   }

}