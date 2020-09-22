package maquette.core.config;

import akka.actor.ActorSystem;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import maquette.core.entities.infrastructure.InfrastructureManager;
import maquette.core.entities.processes.ProcessManager;
import maquette.core.entities.project.Projects;

@Getter
@AllArgsConstructor(staticName = "apply")
public final class RuntimeConfiguration {

    private final Javalin app;

    private final ActorSystem system;

    private final ObjectMapper objectMapper;

    private final InfrastructureManager infrastructureManager;

    private final ProcessManager processManager;

    private final Projects projects;

}
