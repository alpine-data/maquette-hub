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

   @SuppressWarnings("unused")
   private MlflowConfiguration() {
      this.deploymentName = "";
      this.minioAccessKey = "";
      this.minioSecretKey = "";
      this.postgresPassword = "";
      this.postgresUsername = "";
   }

   public static MlflowConfiguration apply(UID project) {
      var deploymentId = String.format("mq__%s__mlflow", project);
      var minioAccessKey = Operators.randomHash();
      var minioSecretKey = Operators.randomHash();
      var postgresPassword = Operators.randomHash();
      var postgresUsername = Operators.randomHash();

      return apply(deploymentId, minioAccessKey, minioSecretKey, postgresPassword, postgresUsername);
   }

}
