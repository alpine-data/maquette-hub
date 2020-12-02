package maquette.core.entities.infrastructure.model;

import maquette.common.Operators;

public final class DeploymentConfigs {

   private DeploymentConfigs() {

   }

   public static DeploymentConfig sample(String projectId, String sandboxId) {
      var postgresContainerCfg = ContainerConfig
         .builder(String.format("mq__%s_%s__psql", projectId, sandboxId), "postgres:12.4")
         .withEnvironmentVariable("POSTGRES_USER", Operators.hash())
         .withEnvironmentVariable("POSTGRES_PASSWORD", Operators.hash())
         .withEnvironmentVariable("PGDATA", "/data")
         .withPort(5432)
         .build();

      var pgAdminConfig = ContainerConfig
         .builder(String.format("mq__%s_%s__pgadmin", projectId, sandboxId), "dpage/pgadmin4:latest")
         .withEnvironmentVariable("PGADMIN_DEFAULT_EMAIL", "maquette")
         .withEnvironmentVariable("PGADMIN_DEFAULT_PASSWORD", "password")
         .withPort(9000)
         .withCommand("server /data")
         .build();

      return DeploymentConfig
         .builder(String.format("mq__%s", projectId))
         .withContainerConfig(postgresContainerCfg)
         .withContainerConfig(pgAdminConfig)
         .build();
   }

}
