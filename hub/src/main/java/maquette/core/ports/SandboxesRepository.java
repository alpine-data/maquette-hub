package maquette.core.ports;

import akka.Done;
import maquette.core.entities.projects.model.sandboxes.SandboxProperties;
import maquette.core.values.UID;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface SandboxesRepository {

   CompletionStage<Optional<SandboxProperties>> findSandboxById(UID project, UID sandbox);

   CompletionStage<Optional<SandboxProperties>> findSandboxByName(UID project, String sandbox);

   CompletionStage<Done> insertOrUpdateSandbox(UID project, SandboxProperties sandbox);

   CompletionStage<List<SandboxProperties>> listSandboxes(UID project);

}
