package maquette.core.entities.infrastructure.model;

public final class DeploymentConfigs {

   private DeploymentConfigs() {

   }

   public static DeploymentConfig sample(String projectId, String sandboxId) {
      var postgresContainerCfg = ContainerConfig
         .builder(String.format("mq__%s_%s__psql", projectId, sandboxId), "postgres:12.4")
         .withEnvironmentVariable("POSTGRES_USER", "postgres")
         .withEnvironmentVariable("POSTGRES_PASSWORD", "password")
         .withEnvironmentVariable("PGDATA", "/data")
         .withPort(5432)
         .build();

      var minioContainerCfg = ContainerConfig
         .builder(String.format("mq__%s_%s__minio", projectId, sandboxId), "minio/minio:latest")
         .withEnvironmentVariable("MINIO_ACCESS_KEY", "maquette")
         .withEnvironmentVariable("MINIO_SECRET_KEY", "password")
         .withPort(9000)
         .withCommand("server /data")
         .build();

      return DeploymentConfig
         .builder(String.format("mq__%s", projectId))
         .withContainerConfig(postgresContainerCfg)
         .withContainerConfig(minioContainerCfg)
         .build();
   }

}
