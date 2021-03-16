package maquette.core.config;

import akka.actor.ActorSystem;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import maquette.core.entities.data.assets_v2.DataAssetEntities;
import maquette.core.entities.data.assets_v2.DataAssetProviders;
import maquette.core.entities.data.collections.CollectionEntities;
import maquette.core.entities.data.datasets.DatasetEntities;
import maquette.core.entities.data.datasources.DataSourceEntities;
import maquette.core.entities.data.streams.StreamEntities;
import maquette.core.entities.dependencies.Dependencies;
import maquette.core.entities.infrastructure.InfrastructureManager;
import maquette.core.entities.logs.Logs;
import maquette.core.entities.processes.ProcessManager;
import maquette.core.entities.projects.ProjectEntities;
import maquette.core.entities.sandboxes.SandboxEntities;
import maquette.core.entities.users.UserEntities;

@Getter
@AllArgsConstructor(staticName = "apply")
public final class RuntimeConfiguration {

    private final Javalin app;

    private final ActorSystem system;

    private final ObjectMapper objectMapper;

    private final DataAssetEntities dataAssets;

    private final DataAssetProviders providers;

    private final CollectionEntities collections;

    private final DatasetEntities datasets;

    private final DataSourceEntities dataSources;

    private final StreamEntities streams;

    private final InfrastructureManager infrastructureManager;

    private final ProcessManager processManager;

    private final ProjectEntities projects;

    private final SandboxEntities sandboxes;

    private final UserEntities users;

    private final Dependencies dependencies;

    private final Logs logs;

}
