package maquette.adapters.projects;

import akka.Done;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import maquette.core.entities.project.model.ProjectSummary;
import maquette.core.ports.ProjectsRepository;
import maquette.core.values.ActionMetadata;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.GrantedAuthorization;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.glassfish.jersey.internal.guava.Sets;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class InMemoryProjectsRepository implements ProjectsRepository {

    private final Map<String, Pair<ProjectSummary, Set<GrantedAuthorization>>> projects;

    public static InMemoryProjectsRepository apply() {
        return apply(Maps.newHashMap());
    }

    @Override
    public CompletionStage<Done> addGrantedAuthorization(String projectId, GrantedAuthorization authorization) {
        if (projects.containsKey(projectId)) {
            projects.get(projectId).getRight().add(authorization);
        }

        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<List<GrantedAuthorization>> getGrantedAuthorizations(String projectId) {
        if (projects.containsKey(projectId)) {
            return CompletableFuture.completedFuture(List.copyOf(projects.get(projectId).getRight()));
        } else {
            return CompletableFuture.completedFuture(Lists.newArrayList());
        }
    }

    @Override
    public CompletionStage<Done> removeGrantedAuthorization(String projectId, Authorization authorization) {
        if (projects.containsKey(projectId)) {
            var project = projects.get(projectId);
            var authorizations = project.getRight().stream().filter(a -> !a.getAuthorization().equals(authorization)).collect(Collectors.toSet());
            projects.put(projectId, Pair.of(project.getLeft(), authorizations));
        } else {
            return CompletableFuture.completedFuture(Done.getInstance());
        }

        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<Optional<ProjectSummary>> findProjectById(String id) {
        if (projects.containsKey(id)) {
            return CompletableFuture.completedFuture(Optional.of(projects.get(id).getLeft()));
        } else {
            return CompletableFuture.completedFuture(Optional.empty());
        }
    }

    @Override
    public CompletionStage<Optional<ProjectSummary>> findProjectByName(String name) {
        var result = projects.values().stream()
           .map(Pair::getLeft)
           .filter(p -> p.getName().equals(name))
           .findFirst();

        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<ProjectSummary> getProjectById(String id) {
        return CompletableFuture.completedFuture(projects.get(id).getLeft());
    }

    @Override
    public CompletionStage<Done> insertOrUpdateProject(ProjectSummary project) {
        if (projects.containsKey(project.getId())) {
            var p = projects.get(project.getId());
            projects.put(project.getId(), Pair.of(project, p.getRight()));
        } else {
            projects.put(project.getId(), Pair.of(project, Sets.newHashSet()));
        }

        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<List<ProjectSummary>> getProjects() {
        var result = projects.values().stream().map(Pair::getLeft).collect(Collectors.toList());
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<Done> updateLastModified(String projectId, ActionMetadata modified) {
        if (projects.containsKey(projectId)) {
            var p = projects.get(projectId);

            projects.put(projectId, Pair.of(p.getLeft().withModified(modified), p.getRight()));
        }

        return CompletableFuture.completedFuture(Done.getInstance());
    }

}
