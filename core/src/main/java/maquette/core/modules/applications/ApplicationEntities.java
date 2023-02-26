package maquette.core.modules.applications;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import maquette.core.modules.applications.model.Application;
import maquette.core.modules.ports.ApplicationsRepository;
import maquette.core.values.UID;
import maquette.core.values.user.ApplicationUser;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class ApplicationEntities {

    private final ApplicationsRepository repository;

    private final ObjectMapper om;

    /**
     * Get application user by application ID and secret
     * @param id application ID
     * @param secret application secret
     * @return application user
     */
    public CompletionStage<Optional<ApplicationUser>> getApplicationUserByIdAndSecret(UID id, String secret) {
        return repository
            .findByIdAndSecret(id, secret)
            .thenApply(result ->
                result.map(app -> ApplicationUser.apply(app.getId(), Lists.newArrayList()))
            );
    }

    /**
     * Get application by its name and workspace it belongs to
     * @param appName application name
     * @param workspaceId workspace ID
     * @return application
     */
    public CompletionStage<Optional<Application>> getApplicationByNameAndWorkspaceId(String appName, UID workspaceId) {
        return repository.findByNameAndWorkspaceId(appName, workspaceId);
    }

    /**
     * Remove an application
     * @param application application to remove
     * @return Done.
     */
    public CompletionStage<Done> removeApplication(Application application) {
        return repository.remove(application);
    }

    /**
     * Find all applications within a workspace
     * @param workspaceId workspace ID
     * @return list of applications
     */
    public CompletionStage<List<Application>> findByWorkspaceId(UID workspaceId) {
        return repository.findByWorkspaceId(workspaceId);
    }

    /**
     * Insert or update an application
     * @param application application to save
     * @return Done.
     */
    public CompletionStage<Done> save(Application application) {
        return repository.save(application);
    }
}
