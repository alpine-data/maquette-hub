package maquette.development.ports;

import akka.Done;
import maquette.core.ports.HasMembers;
import maquette.core.values.UID;
import maquette.development.values.WorkspaceMemberRole;
import maquette.development.values.WorkspaceProperties;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public interface WorkspacesRepository extends HasMembers<WorkspaceMemberRole> {

    /**
     * Search for a project by a given project id.
     *
     * @param id The project's unique id
     * @return An optional project-memento; None of no project with id has been found
     */
    CompletionStage<Optional<WorkspaceProperties>> findWorkspaceById(UID id);

    /**
     * Search a project by its name.
     *
     * @param name The expected name of the project.
     * @return The properties if found, else None.
     */
    CompletionStage<Optional<WorkspaceProperties>> findWorkspaceByName(String name);

    /**
     * Inserts or updates a project in the repository (decision based on id).
     *
     * @param project The project to store
     * @return Done if db update was successful
     */
    CompletionStage<Done> insertOrUpdateWorkspace(WorkspaceProperties project);

    /**
     * Retrieve a list of all available projects.
     *
     * @return A list of projects
     */
    CompletionStage<Stream<WorkspaceProperties>> getWorkspaces();

    /**
     * Delete all metadata of a project.
     *
     * @param project The unique id of the project.
     * @return Done.
     */
    CompletionStage<Done> removeWorkspace(UID project);

}
