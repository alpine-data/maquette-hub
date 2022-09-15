package maquette.development.ports;

import akka.Done;
import maquette.core.values.UID;
import maquette.development.values.exceptions.SandboxNotFoundException;
import maquette.development.values.sandboxes.SandboxProperties;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface SandboxesRepository {

    /**
     * Find a sandbox by its workspace and unique id.
     *
     * @param workspace The unique id of the workspace.
     * @param sandbox   The unique id of the sandbox.
     * @return The sandbox, if found.
     */
    CompletionStage<Optional<SandboxProperties>> findSandboxById(UID workspace, UID sandbox);

    /**
     * Find a sandbox by its workspace and unique id.
     *
     * @param workspace The unique id of the workspace.
     * @param sandbox   The unique id of the sandbox.
     * @return The sandbox. Will throw exception if not found.
     */
    default CompletionStage<SandboxProperties> getSandboxById(UID workspace, UID sandbox) {
        return findSandboxById(workspace, sandbox).thenApply(
            sdbx -> sdbx.orElseThrow(() -> SandboxNotFoundException.applyFromId(sandbox)));
    }

    /**
     * Find a sandbox by its name and its related workspace.
     *
     * @param workspace The unique id of the workspace.
     * @param sandbox   The name of the sandbox.
     * @return The sandbox if found.
     */
    CompletionStage<Optional<SandboxProperties>> findSandboxByName(UID workspace, String sandbox);

    /**
     * Find a sandbox by its name and its related workspace.
     *
     * @param workspace The unique id of the workspace.
     * @param sandbox   The name of the sandbox.
     * @return The sandbox. Will throw exception if not found.
     */
    default CompletionStage<SandboxProperties> getSandboxByName(UID workspace, String sandbox) {
        return findSandboxByName(workspace, sandbox).thenApply(
            sdbx -> sdbx.orElseThrow(() -> SandboxNotFoundException.applyFromName(sandbox)));
    }

    /**
     * Inserts or updates a sandbox configuration.
     *
     * @param workspace The unique id of the workspace the sandbox belong to.
     * @param sandbox   The sandbox properties.
     * @return Done.
     */
    CompletionStage<Done> insertOrUpdateSandbox(UID workspace, SandboxProperties sandbox);

    /**
     * Retrieve a list of all sandboxes of a workspace.
     *
     * @param workspace The unique id of the workspace.
     * @return The list of existing sandboxes.
     */
    CompletionStage<List<SandboxProperties>> listSandboxes(UID workspace);

    /**
     * Removes a sandbox from the store.
     *
     * @param workspace The unique id of the workspace the sandbox belongs to.
     * @param sandbox   The unique id of the sandbox to remove.
     * @return Done.
     */
    CompletionStage<Done> removeSandboxById(UID workspace, UID sandbox);

    /**
     * Removes a sandbox from the store, selected by its unique name.
     *
     * @param workspace The unique id of the workspace the sandbox belongs to.
     * @param name      The name of the sandbox.
     * @return Done.
     */
    CompletionStage<Done> removeSandboxByName(UID workspace, String name);

}
