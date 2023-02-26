package maquette.core.modules.ports;

import akka.Done;
import maquette.core.modules.applications.model.Application;
import maquette.core.values.UID;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ApplicationsRepository {

    /**
     * Find application by its ID and secret
     * @param id application ID
     * @param secret application secret
     * @return application
     */
    CompletionStage<Optional<Application>> findByIdAndSecret(UID id, String secret);

    /**
     * Find application by its name and workspace it belongs to
     * @param name application name
     * @param workspaceId workspace ID
     * @return application
     */
    CompletionStage<Optional<Application>> findByNameAndWorkspaceId(String name, UID workspaceId);

    /**
     * Find application by its ID
     * @param id application ID
     * @return application
     */
    CompletionStage<Optional<Application>> findById(UID id);

    /**
     * Find all applications within a workspace
     * @param workspaceId workspace ID
     * @return list of applications
     */
    CompletionStage<List<Application>> findByWorkspaceId(UID workspaceId);

    /**
     * Insert or update an application
     * @param application application to save
     * @return Done.
     */
    CompletionStage<Done> save(Application application);

    /**
     * Remove an application
     * @param application application to remove
     * @return Done.
     */
    CompletionStage<Done> remove(Application application);
}
