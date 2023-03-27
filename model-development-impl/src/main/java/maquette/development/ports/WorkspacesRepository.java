package maquette.development.ports;

import akka.Done;
import maquette.core.ports.HasMembers;
import maquette.core.values.UID;
import maquette.development.values.WorkspaceMemberRole;
import maquette.development.values.WorkspaceProperties;
import maquette.development.values.exceptions.WorkspaceNotFoundException;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public interface WorkspacesRepository extends HasMembers<WorkspaceMemberRole> {

    /**
     * Search for a workspace by a given workspace id.
     *
     * @param id The workspace's unique id
     * @return An optional workspace-memento; None of no workspace with id has been found
     */
    CompletionStage<Optional<WorkspaceProperties>> findWorkspaceById(UID id);

    /**
     * Get a workspace by its workspace id.
     *
     * @param id The workspace's unique id
     * @return An optional workspace-memento; None of no workspace with id has been found
     * @throws WorkspaceNotFoundException if not found,
     */
    default CompletionStage<WorkspaceProperties> getWorkspaceById(UID id) {
        return findWorkspaceById(id).thenApply(
            opt -> opt.orElseThrow(() -> WorkspaceNotFoundException.applyFromId(id)));
    }

    CompletionStage<Stream<WorkspaceProperties>> findAllWorkspaces();

    /**
     * Search a workspace by its name.
     *
     * @param name The expected name of the workspace.
     * @return The properties if found, else None.
     */
    CompletionStage<Optional<WorkspaceProperties>> findWorkspaceByName(String name);

    /**
     * Get a workspace by its name.
     *
     * @param name The expected name of the workspace.
     * @return The properties if found, else None.
     * @throw WorkspaceNotFoundException if workspace not found.
     */
    default CompletionStage<WorkspaceProperties> getWorkspaceByName(String name) {
        return findWorkspaceByName(name).thenApply(
            opt -> opt.orElseThrow(() -> WorkspaceNotFoundException.applyFromName(name)));
    }

    /**
     * Inserts or updates a workspace in the repository (decision based on id).
     *
     * @param workspace The workspace to store
     * @return Done if db update was successful
     */
    CompletionStage<Done> insertOrUpdateWorkspace(WorkspaceProperties workspace);

    /**
     * Retrieve a list of all available workspaces.
     *
     * @return A list of workspaces
     */
    CompletionStage<Stream<WorkspaceProperties>> getWorkspaces();

    /**
     * Delete all metadata of a workspace.
     *
     * @param workspace The unique id of the workspace.
     * @return Done.
     */
    CompletionStage<Done> removeWorkspace(UID workspace);

}
