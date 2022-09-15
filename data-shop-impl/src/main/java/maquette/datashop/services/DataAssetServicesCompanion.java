package maquette.datashop.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.modules.ServicesCompanion;
import maquette.core.values.UID;
import maquette.core.values.user.User;
import maquette.datashop.entities.DataAssetEntities;
import maquette.datashop.entities.DataAssetEntity;
import maquette.datashop.ports.WorkspacesServicePort;
import maquette.datashop.values.DataAssetProperties;
import maquette.datashop.values.access.DataAssetMemberRole;
import maquette.datashop.values.access.DataAssetMembers;
import maquette.datashop.values.access.DataAssetPermissions;
import maquette.datashop.values.access_requests.DataAccessRequest;
import maquette.datashop.values.access_requests.DataAccessRequestProperties;
import maquette.datashop.values.access_requests.DataAccessRequestState;
import maquette.datashop.values.access_requests.LinkedWorkspace;
import maquette.datashop.values.metadata.DataVisibility;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class DataAssetServicesCompanion extends ServicesCompanion {

    private final DataAssetEntities entities;

    private final WorkspacesServicePort workspaces;

    /**
     * Checks whether the user is a member of the data asset.
     *
     * @param user        The user to check.
     * @param asset       The name of the data asset.
     * @param role        The role the user should have.
     * @param passThrough The value which is passed if the user is authorized.
     * @param <T>         The type of the passThrough
     * @return The passThrough or empty.
     */
    public <T> CompletionStage<Optional<T>> filterMember(User user, String asset, DataAssetMemberRole role,
                                                         T passThrough) {
        return entities
            .getByName(asset)
            .thenCompose(d -> d
                .getMembers()
                .getMembers())
            .thenApply(members -> {
                var isMember = members
                    .stream()
                    .anyMatch(granted -> granted
                        .getAuthorization()
                        .authorizes(user) && (Objects.isNull(role) || role.equals(granted.getRole())));

                if (isMember) {
                    return Optional.of(passThrough);
                } else {
                    return Optional.empty();
                }
            });
    }

    /**
     * Allows to check whether the user has a specific permission for the data asset.
     *
     * @param user        The user to check.
     * @param name        The name of the data asset.
     * @param check       The function which checks the permission.
     * @param passThrough The value which is passed if the user is authorized.
     * @param <T>         The type of the passThrough
     * @return The passThrough or empty.
     */
    public <T> CompletionStage<Optional<T>> filterPermission(User user, String name, Function<DataAssetPermissions,
        Boolean> check, T passThrough) {
        var entityCS = entities.getByName(name);
        var propertiesCS = entityCS.thenCompose(DataAssetEntity::getProperties);
        var accessRequestsCS = Operators
            .compose(entityCS, propertiesCS, (entity, properties) -> entity
                .getAccessRequests()
                .getDataAccessRequests()
                .thenCompose(requests -> Operators.allOf(requests
                    .stream()
                    .map(request -> enrichDataAccessRequest(properties, request)))))
            .thenCompose(cs -> cs);
        var membersCS = entityCS.thenCompose(entity -> entity
            .getMembers()
            .getMembers());
        var workspaceUIDsCS = workspaces.getWorkspacesByMember(user);

        return Operators.compose(accessRequestsCS, membersCS, workspaceUIDsCS,
            (accessRequests, members, workspaceUIDs) -> {
                // filter access requests to the set of requests which can only be seen by the user.
                accessRequests = accessRequests
                    .stream()
                    .filter(request -> workspaceUIDs
                        .stream()
                        .anyMatch(ws -> ws.equals(request
                            .getWorkspace()
                            .getId())))
                    .collect(Collectors.toList());

                var membersCompanion = DataAssetMembers.apply(accessRequests, members);
                var result = check.apply(membersCompanion.getDataAssetPermissions(user));

                if (result) {
                    return Optional.of(passThrough);
                } else {
                    return Optional.empty();
                }
            });
    }

    /**
     * Returns the passThrough of the user is member of a workspace which is the origin of the access request.
     *
     * @param user          The user to check.
     * @param name          The name of the data asset.
     * @param accessRequest The id of the access request.
     * @param passThrough   The passThrough.
     * @param <T>           The type of the passThrough.
     * @return The passThrough if user is authorized, else empty.
     */
    public <T> CompletionStage<Optional<T>> filterRequester(User user, String name, UID accessRequest, T passThrough) {
        return entities
            .getByName(name)
            .thenCompose(d -> d
                .getAccessRequests()
                .getDataAccessRequestById(accessRequest))
            .thenCompose(r -> workspaces
                .getWorkspacesByMember(user)
                .thenApply(workspaces -> workspaces
                    .stream()
                    .anyMatch(w -> w
                        .getId()
                        .equals(r.getWorkspace()))))
            .thenApply(isMember -> {
                if (isMember) {
                    return Optional.of(passThrough);
                } else {
                    return Optional.empty();
                }
            });
    }

    /**
     * Returns the pass through if the user is member of the origin workspace and the workspace is allowed to access
     * the data.
     *
     * @param user        The user who is trying to access the data.
     * @param name        The name of the asset.
     * @param workspace   The workspace which might have access to the asset.
     * @param passThrough The pass through.
     * @param <T>         The type of the pass through.
     * @return The pass through or Optional.empty()
     */
    public <T> CompletionStage<Optional<T>> filterSubscribedConsumer(User user, String name, UID workspace,
                                                                     T passThrough) {
        var requestsCS = entities
            .getByName(name)
            .thenCompose(ds -> ds
                .getAccessRequests()
                .getDataAccessRequests());

        var workspacesCS = workspaces
            .getWorkspacesByMember(user)
            .thenApply(workspaces -> workspaces
                .stream()
                .filter(wks -> wks.equals(workspace))
                .collect(Collectors.toList()));

        return Operators.compose(requestsCS, workspacesCS, (requests, workspaces) -> {
            var request = requests
                .stream()
                .filter(r -> r
                    .getState()
                    .equals(DataAccessRequestState.GRANTED))
                .anyMatch(r -> workspaces
                    .stream()
                    .anyMatch(workspaceUID -> workspaceUID.equals(r.getWorkspace())));

            if (request) {
                return Optional.of(passThrough);
            } else {
                return Optional.empty();
            }
        });
    }

    /**
     * Returns the pass through if the user is member of any project which is allowed to access the data asset.
     *
     * @param user        The user who is trying to access the data.
     * @param name        The name of the asset.
     * @param passThrough The pass through.
     * @param <T>         The type of the pass through.
     * @return The pass through or Optional.empty()
     */
    public <T> CompletionStage<Optional<T>> filterSubscribedConsumer(User user, String name, T passThrough) {
        var requestsCS = entities
            .getByName(name)
            .thenCompose(ds -> ds
                .getAccessRequests()
                .getDataAccessRequests());

        var workspacesCS = workspaces.getWorkspacesByMember(user);

        return Operators.compose(requestsCS, workspacesCS, (requests, workspaces) -> {
            var request = requests
                .stream()
                .filter(r -> r
                    .getState()
                    .equals(DataAccessRequestState.GRANTED))
                .anyMatch(r -> workspaces
                    .stream()
                    .anyMatch(workspaceUID -> workspaceUID
                        .getId()
                        .equals(r.getWorkspace())));

            if (request) {
                return Optional.of(passThrough);
            } else {
                return Optional.empty();
            }
        });
    }

    /**
     * Returns the passThrough value if the data asset is visible.
     *
     * @param name        The name of the data asset to check.
     * @param passThrough The passThrough value.
     * @param <T>         The type of the passThrough.
     * @return The passThrough or empty, if the data asset is not visible.
     */
    public <T> CompletionStage<Optional<T>> filterVisible(String name, T passThrough) {
        return entities
            .getByName(name)
            .thenCompose(d -> d
                .getProperties()
                .thenApply(properties -> {
                    if (properties
                        .getMetadata()
                        .getVisibility()
                        .equals(DataVisibility.PUBLIC)) {
                        return Optional.of(passThrough);
                    } else {
                        return Optional.empty();
                    }
                }));
    }

    /**
     * Check whether a user has a specific permission for a data asset.
     *
     * @param user  The user to check.
     * @param name  The name of the data asset.
     * @param check The function which checks the user's permissions.
     * @return True if authorized.
     */
    public CompletionStage<Boolean> hasPermission(User user, String name,
                                                  Function<DataAssetPermissions, Boolean> check) {
        return filterPermission(user, name, check, Done.getInstance()).thenApply(Optional::isPresent);
    }

    /**
     * Checks whether the user is a member of a data asset.
     *
     * @param user The user to check.
     * @param name The name of the data asset.
     * @return True if authorized.
     */
    public CompletionStage<Boolean> isMember(User user, String name) {
        return isMember(user, name, null);
    }

    /**
     * Checks whether the user is a member of a data asset and owns a specific role.
     *
     * @param user The user to check.
     * @param name The name of the data asset.
     * @param role The role the user must have.
     * @return True if authorized.
     */
    public CompletionStage<Boolean> isMember(User user, String name, DataAssetMemberRole role) {
        return filterMember(user, name, role, Done.getInstance()).thenApply(Optional::isPresent);
    }

    /**
     * Checks whether a user is member of the workspace of the provided access request.
     *
     * @param user          The user to check.
     * @param name          The name of the data asset.
     * @param accessRequest The id of the access request.
     * @return True if the user is member of the workspace. Otherwise False.
     */
    public CompletionStage<Boolean> isRequester(User user, String name, UID accessRequest) {
        return filterRequester(user, name, accessRequest, Done.getInstance()).thenApply(Optional::isPresent);
    }

    /**
     * Checks whether the user is a member of a subscribed workspace of the data asset. A workspace
     * is subscribed if there is a granted access request for the workspace towards the data asset.
     *
     * @param user      The user to check.
     * @param name      The name of the data asset.
     * @param workspace The id of the workspace which is granted to the data asset.
     * @return True if user is member of a granted workspace.
     */
    public CompletionStage<Boolean> isSubscribedConsumer(User user, String name, UID workspace) {
        return filterSubscribedConsumer(user, name, workspace, Done.getInstance()).thenApply(Optional::isPresent);
    }

    /**
     * Checks whether the user is a member of a subscribed workspace of the data asset. A workspace
     * is subscribed if there is a granted access request for the workspace towards the data asset.
     *
     * @param user The user to check.
     * @param name The name of the data asset.
     * @return True if user is member of a granted workspace.
     */
    public CompletionStage<Boolean> isSubscribedConsumer(User user, String name) {
        return filterSubscribedConsumer(user, name, Done.getInstance()).thenApply(Optional::isPresent);
    }

    /**
     * Checks whether a data asset is visible.
     *
     * @param name The name of the data asset.
     * @return True if data asset is visible.
     */
    public CompletionStage<Boolean> isVisible(String name) {
        return filterVisible(name, Done.getInstance()).thenApply(Optional::isPresent);
    }

    /**
     * Load linked data for a data access request.
     *
     * @param asset The data asset properties to which the access request belongs.
     * @param req   The access request which should be enriched.
     * @return The enriched request.
     */
    public CompletionStage<DataAccessRequest> enrichDataAccessRequest(DataAssetProperties asset,
                                                                      DataAccessRequestProperties req) {
        return workspaces
            .getWorkspacePropertiesByWorkspaceId(req.getWorkspace())
            .thenApply(workspaceProperties -> {
                var linkedWorkspace = LinkedWorkspace.apply(req.getId(), workspaceProperties);
                return DataAccessRequest.apply(req.getId(), req.getCreated(), asset, linkedWorkspace, req.getEvents(),
                    req.getState());
            });
    }


}
