package maquette.adapters.projects;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.entities.project.model.ProjectSummary;
import maquette.core.ports.ProjectsRepository;
import maquette.core.values.ActionMetadata;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.GrantedAuthorization;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public class InMemoryProjectsRepository implements ProjectsRepository {

    @Override
    public CompletionStage<Done> addGrantedAuthorization(String projectId, GrantedAuthorization authorization) {
        return null;
    }

    @Override
    public CompletionStage<List<GrantedAuthorization>> getGrantedAuthorizations(String projectId) {
        return null;
    }

    @Override
    public CompletionStage<Done> removeGrantedAuthorization(String projectId, Authorization authorization) {
        return null;
    }

    @Override
    public CompletionStage<Optional<ProjectSummary>> findProjectById(String id) {
        return null;
    }

    @Override
    public CompletionStage<Optional<ProjectSummary>> findProjectByName(String name) {
        return null;
    }

    @Override
    public CompletionStage<ProjectSummary> getProjectById(String id) {
        return null;
    }

    @Override
    public CompletionStage<Done> insertOrUpdateProject(ProjectSummary project) {
        return null;
    }

    @Override
    public CompletionStage<List<ProjectSummary>> getProjects() {
        return null;
    }

    @Override
    public CompletionStage<Done> updateLastModified(String projectId, ActionMetadata modified) {
        return null;
    }

}
