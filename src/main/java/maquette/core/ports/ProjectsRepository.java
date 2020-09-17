package maquette.core.ports;

import akka.Done;
import maquette.core.entities.ProjectMemento;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ProjectsRepository {

    /**
     * Search for a project by a given project id.
     *
     * @param id The project's unique id
     * @return An optional project-memento; None of no project with id has been found
     */
    CompletionStage<Optional<ProjectMemento>> findProjectById(String id);

    /**
     * Same as {@link ProjectsRepository#findProjectById(String)} except that it will throw
     * an exception if project is not found.
     *
     * @param id The project's unique id
     * @return The project's memento
     */
    CompletionStage<ProjectMemento> getProjectById(String id);

    /**
     * Inserts or updates a project in the repository (decision based on id).
     *
     * @param project The project to store
     * @return Done if db update was successful
     */
    CompletionStage<Done> insertOrUpdateProject(ProjectMemento project);

    /**
     * Retrieve a list of all available projects.
     *
     * @return A list of projects
     */
    CompletionStage<List<ProjectMemento>> getProjects();

}
