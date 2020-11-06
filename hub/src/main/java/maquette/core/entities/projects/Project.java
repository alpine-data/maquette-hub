package maquette.core.entities.projects;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.projects.model.ProjectDetails;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.ports.ProjectsRepository;
import maquette.core.values.ActionMetadata;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.user.User;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class Project {

    private final ProjectsRepository repository;

    private final String id;

    public CompletionStage<GrantedAuthorization> grant(User executor, Authorization authorization) {
        return repository
                .getGrantedAuthorizations(id)
                .thenCompose(authorizations -> {
                    var existing = authorizations
                            .stream()
                            .filter(a -> a.getAuthorization().equals(authorization))
                            .findAny();

                    if (existing.isPresent()) {
                        return CompletableFuture.completedFuture(existing.get());
                    } else {
                        var modified = ActionMetadata.apply(executor, Instant.now());
                        var granted = GrantedAuthorization.apply(
                                modified,
                                authorization);

                        return repository
                                .addGrantedAuthorization(id, granted)
                                .thenCompose(done -> repository.updateLastModified(id, modified))
                                .thenApply(done -> granted);
                    }
                });
    }

    public CompletionStage<Done> revoke(User executor, Authorization authorization) {
        return repository
                .removeGrantedAuthorization(id, authorization)
                .thenCompose(done -> {
                    var modified = ActionMetadata.apply(executor, Instant.now());
                    return repository.updateLastModified(id, modified);
                });
    }

    public CompletionStage<ProjectDetails> getDetails() {
        return Operators
                .compose(
                        getProperties(),
                        repository.getGrantedAuthorizations(id),
                        (props, authorizations) -> ProjectDetails.apply(
                           id, props.getName(), props.getTitle(), props.getSummary(),
                           props.getCreated(), props.getModified(), authorizations));
    }

    public String getId() {
       return id;
    }

    public CompletionStage<ProjectProperties> getProperties() {
        return repository
                .findProjectById(id)
                .thenApply(Optional::orElseThrow);
    }

}
