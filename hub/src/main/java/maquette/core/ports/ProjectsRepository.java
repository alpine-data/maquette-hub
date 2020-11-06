package maquette.core.ports;

import akka.Done;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.values.ActionMetadata;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.GrantedAuthorization;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ProjectsRepository {

    CompletionStage<Done> addGrantedAuthorization(String projectId, GrantedAuthorization authorization);

    CompletionStage<List<GrantedAuthorization>> getGrantedAuthorizations(String projectId);

    CompletionStage<Done> removeGrantedAuthorization(String projectId, Authorization authorization);

    /**
     * Search for a project by a given project id.
     *
     * @param id The project's unique id
     * @return An optional project-memento; None of no project with id has been found
     */
    CompletionStage<Optional<ProjectProperties>> findProjectById(String id);

    CompletionStage<Optional<ProjectProperties>> findProjectByName(String name);

    /**
     * Same as {@link ProjectsRepository#findProjectById(String)} except that it will throw
     * an exception if project is not found.
     *
     * @param id The project's unique id
     * @return The project's memento
     */
    CompletionStage<ProjectProperties> getProjectById(String id);

    /**
     * Inserts or updates a project in the repository (decision based on id).
     *
     * @param project The project to store
     * @return Done if db update was successful
     */
    CompletionStage<Done> insertOrUpdateProject(ProjectProperties project);

    /**
     * Retrieve a list of all available projects.
     *
     * @return A list of projects
     */
    CompletionStage<List<ProjectProperties>> getProjects();

    CompletionStage<Done> updateLastModified(String projectId, ActionMetadata modified);

    CompletionStage<Done> removeProject(String projectId);

}
