package maquette.development.entities;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.user.User;
import maquette.development.ports.SandboxesRepository;
import maquette.development.ports.infrastructure.InfrastructurePort;
import maquette.development.values.exceptions.SandboxNotFoundException;
import maquette.development.values.sandboxes.SandboxProperties;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class SandboxEntities {

    private final SandboxesRepository sandboxes;

    private final InfrastructurePort infrastructurePort;

    /**
     * Creates a new sandbox.
     *
     * @param executor  The user who executes the action.
     * @param workspace The id of the workspace the sandbox belongs to.
     * @param volume    The id of the volume the sandbox belongs to.
     * @param name      The name of the sandbox (unique to project).
     * @param comment   A comment describing the purpose of this sandbox.
     * @return The properties of the newly created sandbox.
     */
    public CompletionStage<SandboxProperties> createSandbox(
        User executor, UID workspace, UID volume, String name, String comment) {
        var sandbox = SandboxProperties.apply(UID.apply(), workspace, volume, name, comment, ActionMetadata.apply(executor));

        return sandboxes
            .insertOrUpdateSandbox(workspace, sandbox)
            .thenApply(done -> sandbox);
    }

    /**
     * Find a sandbox by its workspace and unique id.
     *
     * @param workspace The unique id of the workspace.
     * @param sandbox   The unique id of the sandbox.
     * @return The sandbox, if found.
     */
    public CompletionStage<Optional<SandboxEntity>> findSandboxById(UID workspace, UID sandbox) {
        return sandboxes.findSandboxById(workspace, sandbox)
            .thenApply(opt -> opt.map(sdbx -> SandboxEntity.apply(sandboxes, infrastructurePort, sandbox, workspace)));
    }

    /**
     * Find a sandbox by its workspace and unique id.
     *
     * @param workspace The unique id of the workspace.
     * @param sandbox   The unique id of the sandbox.
     * @return The sandbox. Will throw exception if not found.
     */
    public CompletionStage<SandboxEntity> getSandboxById(UID workspace, UID sandbox) {
        return findSandboxById(workspace, sandbox).thenApply(sdbx -> sdbx.orElseThrow(() -> SandboxNotFoundException.applyFromId(sandbox)));
    }

    /**
     * Find a sandbox by its name and its related workspace.
     *
     * @param workspace The unique id of the workspace.
     * @param sandbox   The name of the sandbox.
     * @return The sandbox if found.
     */
    public CompletionStage<Optional<SandboxEntity>> findSandboxByName(UID workspace, String sandbox) {
        return sandboxes.findSandboxByName(workspace, sandbox)
            .thenApply(opt -> opt.map(sdbx -> SandboxEntity.apply(sandboxes, infrastructurePort, sdbx.getId(), workspace)));
    }

    /**
     * Find a sandbox by its name and its related workspace.
     *
     * @param workspace The unique id of the workspace.
     * @param sandbox   The name of the sandbox.
     * @return The sandbox. Will throw exception if not found.
     */
    public CompletionStage<SandboxEntity> getSandboxByName(UID workspace, String sandbox) {
        return findSandboxByName(workspace, sandbox).thenApply(sdbx -> sdbx.orElseThrow(() -> SandboxNotFoundException.applyFromName(sandbox)));
    }

    /**
     * Retrieve a list of all sandboxes of a workspace.
     *
     * @param workspace The unique id of the workspace.
     * @return The list of existing sandboxes.
     */
    public CompletionStage<List<SandboxProperties>> listSandboxes(UID workspace) {
        return sandboxes.listSandboxes(workspace);
    }

    /**
     * Removes a sandbox from the store.
     *
     * @param workspace The unique id of the workspace the sandbox belongs to.
     * @param sandbox   The unique id of the sandbox to remove.
     * @return Done.
     */
    public CompletionStage<Done> removeSandboxById(UID workspace, UID sandbox) {
        return sandboxes.removeSandboxById(workspace, sandbox);
    }

    /**
     * Removes a sandbox from the store, selected by its unique name.
     *
     * @param workspace The unique id of the workspace the sandbox belongs to.
     * @param name      The name of the sandbox.
     * @return Done.
     */
    public CompletionStage<Done> removeSandboxByName(UID workspace, String name) {
        return sandboxes.removeSandboxByName(workspace, name);
    }

}
