package maquette.core.entities.projects;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.projects.model.ProjectSummary;
import maquette.core.ports.ProjectsRepository;
import maquette.core.values.ActionMetadata;
import maquette.core.values.user.User;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class Projects {

    private final ProjectsRepository repository;

    public CompletionStage<String> createProject(User executor, String name) {
        return findProjectByName(name)
                .thenCompose(maybeProject -> {
                    if (maybeProject.isPresent()) {
                        throw new RuntimeException("Project already exists!");
                    } else {
                        var id = Operators.hash();
                        var created = ActionMetadata.apply(executor, Instant.now());
                        var summary = ProjectSummary.apply(id, name, created, created);

                        return repository
                                .insertOrUpdateProject(summary)
                                .thenApply(done -> id);
                    }
                });
    }

    public CompletionStage<Optional<Project>> findProjectByName(String name) {
        return repository
                .findProjectByName(name)
                .thenApply(maybeProject -> maybeProject.map(project -> Project.apply(repository, project.getId())));
    }

    public CompletionStage<Optional<Project>> findProjectById(String id) {
        return repository
                .findProjectById(id)
                .thenApply(maybeProject -> maybeProject.map(project -> Project.apply(repository, project.getId())));
    }

    public CompletionStage<Project> getProjectById(String id) {
        return findProjectById(id).thenApply(Optional::orElseThrow);
    }

    public CompletionStage<List<ProjectSummary>> getProjects() {
        return repository.getProjects();
    }

    public CompletionStage<Done> removeProject(String projectId) {
        return repository.removeProject(projectId);
    }

}