package maquette.datashop.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.common.Operators;
import maquette.core.modules.users.UserModule;
import maquette.core.ports.email.EmailClient;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;
import maquette.datashop.entities.DataAssetEntities;
import maquette.datashop.entities.DataAssetEntity;
import maquette.datashop.ports.Workspace;
import maquette.datashop.ports.WorkspacesServicePort;
import maquette.datashop.providers.DataAssetProviders;
import maquette.datashop.providers.DataAssetSettings;
import maquette.datashop.values.DataAsset;
import maquette.datashop.values.DataAssetProperties;
import maquette.datashop.values.access.DataAssetMemberRole;
import maquette.datashop.values.access_requests.DataAccessRequest;
import maquette.datashop.values.access_requests.DataAccessRequestProperties;
import maquette.datashop.values.metadata.DataAssetMetadata;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class DataAssetServicesImpl implements DataAssetServices {

    private static final Logger LOG = LoggerFactory.getLogger(DataAssetServices.class);

    private final DataAssetEntities entities;

    private final WorkspacesServicePort workspaces;

    private final DataAssetProviders providers;

    private final DataAssetServicesCompanion dataAssetsCompanion;

    private final EmailClient emailClient;

    @Override
    public CompletionStage<DataAssetProperties> create(User executor, String type, DataAssetMetadata metadata,
                                                       Authorization owner, Authorization steward,
                                                       @Nullable Object customSettings) {
        return entities.create(executor, type, metadata, owner, steward, customSettings);
    }

    @Override
    public CompletionStage<DataAsset> get(User executor, String name) {
        return entities
            .getByName(name)
            .thenCompose(entity -> {
                var propertiesCS = entity.getProperties();
                var membersCS = entity
                    .getMembers()
                    .getMembers();

                var accessRequestsRawCS = entity
                    .getAccessRequests()
                    .getDataAccessRequests();

                var accessRequestsCS = Operators
                    .compose(propertiesCS, accessRequestsRawCS, (properties, accessRequestsRaw) -> accessRequestsRaw
                        .stream()
                        .map(request -> dataAssetsCompanion.enrichDataAccessRequest(properties, request)))
                    .thenCompose(Operators::allOf);

                var customSettingsCS = propertiesCS
                    .thenCompose(properties -> {
                        var provider = providers.getByName(properties.getType());
                        return entity.getCustomSettings(provider.getSettingsType());
                    })
                    .thenApply(DataAssetSettings::getObfuscated);

                var customDetailsCS = Operators
                    .compose(propertiesCS, customSettingsCS, (properties, customSettings) -> providers
                        .getByName(properties.getType())
                        .getDetails(properties, customSettings))
                    .thenCompose(cs -> cs);

                var customPropertiesCS = propertiesCS.thenCompose(properties -> {
                    var provider = providers.getByName(properties.getType());
                    return entity.getCustomProperties(provider.getPropertiesType());
                });

                return Operators.compose(
                    propertiesCS, accessRequestsCS, membersCS,
                    customSettingsCS, customPropertiesCS, customDetailsCS, DataAsset::apply);
            });
    }

    @Override
    public CompletionStage<List<DataAssetProperties>> list(User executor) {
        return entities.list();
    }

    @Override
    public CompletionStage<List<DataAssetProperties>> query(User executor, String query) {
        return entities
            .list()
            .thenApply(results -> results
                .stream()
                .filter(result -> {
                    var searchString = new StringBuilder()
                        .append(result
                            .getMetadata()
                            .getName())
                        .append(result
                            .getMetadata()
                            .getSummary())
                        .toString()
                        .toLowerCase();

                    return searchString.contains(query.toLowerCase());
                })
                .collect(Collectors.toList()));
    }

    @Override
    public CompletionStage<Done> approve(User executor, String name) {
        return entities
            .getByName(name)
            .thenCompose(entity -> entity.approve(executor));
    }

    @Override
    public CompletionStage<Done> decline(User executor, String name, String reason) {
        return entities
            .getByName(name)
            .thenCompose(entity -> entity.decline(executor));
    }

    @Override
    public CompletionStage<Done> deprecate(User executor, String name, boolean deprecate) {
        return entities
            .getByName(name)
            .thenCompose(entity -> entity.deprecate(executor, deprecate));
    }

    @Override
    public CompletionStage<Done> update(User executor, String name, DataAssetMetadata metadata) {
        return entities
            .getByName(name)
            .thenCompose(entity -> entity.update(executor, metadata));
    }

    @Override
    public CompletionStage<Done> updateCustomSettings(User executor, String name, Object customSettings) {
        return entities
            .getByName(name)
            .thenCompose(entity -> entity.updateCustomSettings(executor, customSettings));
    }

    @Override
    public CompletionStage<Done> remove(User executor, String name) {
        return entities.removeByName(name);
    }

    @Override
    public CompletionStage<Done> requestReview(User executor, String name, String message) {
        return entities
            .getByName(name)
            .thenCompose(entity -> entity.requestReview(executor));
    }


    @Override
    public CompletionStage<DataAccessRequestProperties> createDataAccessRequest(User executor, String name,
                                                                                String workspace, String reason,
                                                                                MaquetteRuntime runtime) {

        var entityCS = entities.getByName(name);
        var workspaceUIDCS = workspaces.getWorkspaceIdByName(workspace);

        return Operators
            .compose(entityCS, workspaceUIDCS, (entity, workspaceUID) -> entity
                .getAccessRequests()
                .createDataAccessRequest(executor, workspaceUID, reason)
                .thenApply(dataAccessProperty -> {
                    entity
                        .getMembers()
                        .getMembers()
                        .thenApply(
                            members -> {
                                members
                                    .stream()
                                    .forEach(member -> {
                                            sendRequestCreatedEmail(executor, name, runtime, member);
                                        }
                                    );
                                return members;
                            });

                    return dataAccessProperty;
                }))
            .thenCompose(cs -> cs);
    }

    private void sendRequestCreatedEmail(User executor, String name, MaquetteRuntime runtime,
                                         GrantedAuthorization<DataAssetMemberRole> member) {
        runtime
            .getModule(UserModule.class)
            .getServices()
            .getProfile(executor, UID.apply(member
                .getAuthorization()
                .getName()))
            .thenApply(reviewer -> {
                    emailClient.sendEmail(reviewer, "",
                        "Please review a new access request",
                        "Mars: New access request", true, runtime, name);
                    return reviewer;
                }
            );
    }

    @Override
    public CompletionStage<DataAccessRequest> getDataAccessRequest(User executor, String name, UID request) {
        var entityCS = entities.getByName(name);
        var propertiesCS = entityCS.thenCompose(DataAssetEntity::getProperties);
        var accessRequestPropertiesCS = entityCS.thenCompose(a -> a
            .getAccessRequests()
            .getDataAccessRequestById(request));

        return Operators
            .compose(propertiesCS, accessRequestPropertiesCS, dataAssetsCompanion::enrichDataAccessRequest)
            .thenCompose(cs -> cs);
    }

    @Override
    public CompletionStage<List<DataAccessRequestProperties>> getDataAccessRequests(User executor, String name) {
        return entities
            .getByName(name)
            .thenCompose(entity -> entity
                .getAccessRequests()
                .getDataAccessRequests());
    }

    @Override
    public CompletionStage<Done> approveDataAccessRequest(User executor, String name, UID request,
                                                          @Nullable String message, MaquetteRuntime runtime) {

        return entities
            .getByName(name)
            .thenCompose(a -> a
                .getAccessRequests()
                .approveDataAccessRequest(executor, request, message)
                .thenApply(done -> {
                    a
                        .getProperties()
                        .thenApply(props -> props
                            .getMetadata()
                            .getName())
                        .thenApply(dataSetName -> {
                            sendEmailForRequester(executor, request, runtime, a, dataSetName,
                                "Your request has been reviewed by the Data Governor",
                                "Mars: Access request reviewed by Data Governor");
                            return dataSetName;
                        });
                    return done;
                }));
    }

    @Override
    public CompletionStage<Done> grantDataAccessRequest(User executor, String name, UID request,
                                                        @Nullable Instant until, @Nullable String message,
                                                        String environment, boolean downstreamApprovalRequired,
                                                        MaquetteRuntime runtime) {
        return entities
            .getByName(name)
            .thenCompose(a -> a
                .getAccessRequests()
                .grantDataAccessRequest(executor, request, until, message, environment, downstreamApprovalRequired)
                .thenApply(done -> {
                    a
                        .getProperties()
                        .thenApply(prop -> prop
                            .getMetadata()
                            .getName())
                        .thenApply(datasetName -> {
                            return extractDataAssetDatails(executor, request, runtime, a, datasetName);
                        });
                    return done;
                }));
    }

    private String extractDataAssetDatails(User executor, UID request, MaquetteRuntime runtime, DataAssetEntity a,
                                           String datasetName) {
        a
            .getMembers()
            .getMembersWithRole(DataAssetMemberRole.OWNER)
            .thenApply(owners -> {
                String executorId = ((AuthenticatedUser) executor)
                    .getId()
                    .getValue();

                boolean isExecutorOwner = owners
                    .stream()
                    .anyMatch(asset -> StringUtils.equalsAnyIgnoreCase(asset
                        .getAuthorization()
                        .getName(), executorId));

                if (isExecutorOwner) {
                    sendEmailForRequester(executor, request, runtime, a, datasetName,
                        "Your request has been reviewed by the Data Governor",
                        "Mars: Access request reviewed by Data Governor");
                } else {
                    sendEmailForRequester(executor, request, runtime, a, datasetName,
                        "Your request has been reviewed by the deputy. Now waiting for the Data Governor approval.",
                        "Mars: Access request reviewed by Deputy");
                }
                return owners;
            });
        return datasetName;
    }

    private void sendEmailForRequester(User executor, UID request, MaquetteRuntime runtime, DataAssetEntity a,
                                       String datasetName, String emsilText, String title) {
        a
            .getAccessRequests()
            .getDataAccessRequestById(request)
            .thenApply(requestByID -> {
                String by = requestByID
                    .getCreated()
                    .getBy();

                return runtime
                    .getModule(UserModule.class)
                    .getServices()
                    .getProfile(executor, UID.apply(by))
                    .thenApply(profile -> {
                            emailClient.sendEmail(profile, "",
                                emsilText,
                                title, true, runtime,
                                datasetName);
                            return profile;
                        }
                    );
            });
    }

    @Override
    public CompletionStage<Done> rejectDataAccessRequest(User executor, String name, UID request, String reason) {
        return entities
            .getByName(name)
            .thenCompose(a -> a
                .getAccessRequests()
                .rejectDataAccessRequest(executor, request, reason));
    }

    @Override
    public CompletionStage<Done> updateDataAccessRequest(User executor, String name, UID request, String reason) {
        return entities
            .getByName(name)
            .thenCompose(a -> a
                .getAccessRequests()
                .updateDataAccessRequest(executor, request, reason));
    }

    @Override
    public CompletionStage<Done> withdrawDataAccessRequest(User executor, String name, UID request,
                                                           @Nullable String reason) {
        return entities
            .getByName(name)
            .thenCompose(a -> a
                .getAccessRequests()
                .withdrawDataAccessRequest(executor, request, reason));
    }

    @Override
    public CompletionStage<Done> grant(User executor, String name, Authorization member, DataAssetMemberRole role) {
        return entities
            .getByName(name)
            .thenCompose(e -> e
                .getMembers()
                .addMember(executor, member, role));
    }

    @Override
    public CompletionStage<Done> revoke(User executor, String name, Authorization member) {
        return entities
            .getByName(name)
            .thenCompose(e -> e
                .getMembers()
                .removeMember(executor, member));
    }

    @Override
    public CompletionStage<List<Workspace>> getUsersWorkspaces(User executor) {
        return workspaces.getWorkspacesByMember(executor);
    }

}
