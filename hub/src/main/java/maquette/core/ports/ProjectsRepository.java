package maquette.core.ports;

import akka.Done;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.entities.projects.model.ProjectMemberRole;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ProjectsRepository extends HasMembers<ProjectMemberRole> {

    /**
     * Search for a project by a given project id.
     *
     * @param id The project's unique id
     * @return An optional project-memento; None of no project with id has been found
     */
    CompletionStage<Optional<ProjectProperties>> findProjectById(UID id);

    CompletionStage<Optional<ProjectProperties>> findProjectByName(String name);

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

    CompletionStage<Done> removeProject(UID project);

}
