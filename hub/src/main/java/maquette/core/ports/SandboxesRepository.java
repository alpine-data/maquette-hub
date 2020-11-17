package maquette.core.ports;

import akka.Done;
import maquette.core.entities.sandboxes.model.SandboxProperties;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface SandboxesRepository {

   CompletionStage<Optional<SandboxProperties>> findSandboxById(String projectId, String sandboxId);

   CompletionStage<Optional<SandboxProperties>> findSandboxByName(String projectId, String sandboxName);

   CompletionStage<Done> insertOrUpdateSandbox(String projectId, SandboxProperties sandbox);

   CompletionStage<List<SandboxProperties>> listSandboxes(String projectId);

}
