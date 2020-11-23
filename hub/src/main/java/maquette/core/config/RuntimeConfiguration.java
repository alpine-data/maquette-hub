package maquette.core.config;

import akka.actor.ActorSystem;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import maquette.core.entities.data.datasets.Datasets;
import maquette.core.entities.infrastructure.InfrastructureManager;
import maquette.core.entities.processes.ProcessManager;
import maquette.core.entities.projects.Projects;
import maquette.core.entities.sandboxes.Sandboxes;
import maquette.core.entities.users.Users;

@Getter
@AllArgsConstructor(staticName = "apply")
public final class RuntimeConfiguration {

    private final Javalin app;

    private final ActorSystem system;

    private final ObjectMapper objectMapper;

    private final Datasets datasets;

    private final InfrastructureManager infrastructureManager;

    private final ProcessManager processManager;

    private final Projects projects;

    private final Sandboxes sandboxes;

    private final Users users;

}
