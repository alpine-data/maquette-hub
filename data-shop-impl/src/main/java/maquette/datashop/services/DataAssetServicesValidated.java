package maquette.datashop.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.common.validation.api.FluentValidation;
import maquette.core.common.validation.validators.NonEmptyStringValidator;
import maquette.core.common.validation.validators.NotNullValidator;
import maquette.core.common.validation.validators.ObjectValidator;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.AuthorizationValidator;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.user.User;
import maquette.datashop.configuration.DataShopConfiguration;
import maquette.datashop.ports.Workspace;
import maquette.datashop.values.DataAsset;
import maquette.datashop.values.DataAssetProperties;
import maquette.datashop.values.access.DataAssetMemberRole;
import maquette.datashop.values.access_requests.DataAccessRequest;
import maquette.datashop.values.access_requests.DataAccessRequestProperties;
import maquette.datashop.values.metadata.AdditionalProperties;
import maquette.datashop.values.metadata.DataAssetMetadata;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DataAssetServicesValidated implements DataAssetServices {

    private final DataAssetServices delegate;

    private final DataShopConfiguration configuration;

    private static final ObjectValidator<DataAssetMetadata> METADATA_VALIDATOR = ObjectValidator
        .<DataAssetMetadata>build()
        .validate("name", DataAssetMetadata::getName, NonEmptyStringValidator.apply(3))
        .validate("title", DataAssetMetadata::getTitle, NonEmptyStringValidator.apply(3))
        .validate("summary", DataAssetMetadata::getSummary, NonEmptyStringValidator.apply(3))
        .validate("visibility", DataAssetMetadata::getVisibility, NotNullValidator.apply())
        .validate("classification", DataAssetMetadata::getClassification, NotNullValidator.apply())
        .validate("personalInformation", DataAssetMetadata::getPersonalInformation, NotNullValidator.apply())
        .validate("zone", DataAssetMetadata::getZone, NotNullValidator.apply())
        .validate("additionalProperties", DataAssetMetadata::getAdditionalProperties, ObjectValidator
            .<AdditionalProperties>build()
            .validate("timeliness", AdditionalProperties::getTimeliness, NotNullValidator.apply())
            .validate("geography", AdditionalProperties::getGeography, NotNullValidator.apply())
            .validate("bu", AdditionalProperties::getBu, NotNullValidator.apply())
            .validate("lob", AdditionalProperties::getLob, NotNullValidator.apply())
            .required())
        .required();

    @Override
    public CompletionStage<DataAssetProperties> create(User executor, String type, DataAssetMetadata metadata,
                                                       Authorization owner, Authorization steward,
                                                       @Nullable Object customSettings) {

        return FluentValidation
            .apply()
            .validate("executor", executor, NotNullValidator.apply())
            .validate("type", type, NotNullValidator.apply())
            .validate("metadata", metadata, METADATA_VALIDATOR)
            .validate("owner", owner, AuthorizationValidator.apply(UserAuthorization.class, false))
            .validate("steward", owner, AuthorizationValidator.apply(UserAuthorization.class, false))
            .checkAndFail()
            .thenCompose(done -> {
                Authorization oOwner;
                Authorization oSteward;

                if (Objects.isNull(owner)) {
                    oOwner = UserAuthorization.apply(configuration.getDefaultDataOwner());
                } else {
                    oOwner = owner;
                }

                if (Objects.isNull(steward)) {
                    oSteward = executor.toAuthorization();
                } else {
                    oSteward = steward;
                }

                return delegate.create(executor, type, metadata, oOwner, oSteward, customSettings);
            });
    }

    @Override
    public CompletionStage<DataAsset> get(User executor, String name) {
        return FluentValidation
            .apply()
            .validate("executor", executor, NotNullValidator.apply())
            .validate("name", name, NotNullValidator.apply())
            .checkAndFail()
            .thenCompose(done -> delegate.get(executor, name));
    }

    @Override
    public CompletionStage<List<DataAssetProperties>> list(User executor) {
        return delegate.list(executor);
    }

    @Override
    public CompletionStage<List<DataAssetProperties>> query(User executor, String query) {
        return FluentValidation
            .apply()
            .validate("query", query, NotNullValidator.apply())
            .checkAndFail()
            .thenCompose(done -> delegate.query(executor, query));
    }

    @Override
    public CompletionStage<Done> approve(User executor, String name) {
        return FluentValidation
            .apply()
            .validate("executor", executor, NotNullValidator.apply())
            .validate("name", name, NotNullValidator.apply())
            .checkAndFail()
            .thenCompose(done -> delegate.approve(executor, name));
    }

    @Override
    public CompletionStage<Done> decline(User executor, String name, String reason) {
        return FluentValidation
            .apply()
            .validate("executor", executor, NotNullValidator.apply())
            .validate("name", name, NotNullValidator.apply())
            .validate("reason", reason, NonEmptyStringValidator.apply(3))
            .checkAndFail()
            .thenCompose(done -> delegate.decline(executor, name, reason));
    }

    @Override
    public CompletionStage<Done> deprecate(User executor, String name, boolean deprecate) {
        return FluentValidation
            .apply()
            .validate("executor", executor, NotNullValidator.apply())
            .validate("name", name, NotNullValidator.apply())
            .checkAndFail()
            .thenCompose(done -> delegate.deprecate(executor, name, deprecate));
    }

    @Override
    public CompletionStage<Done> update(User executor, String name, DataAssetMetadata metadata) {
        return FluentValidation
            .apply()
            .validate("executor", executor, NotNullValidator.apply())
            .validate("name", name, NotNullValidator.apply())
            .validate("metadata", metadata, METADATA_VALIDATOR)
            .checkAndFail()
            .thenCompose(done -> delegate.update(executor, name, metadata));
    }

    @Override
    public CompletionStage<Done> updateCustomSettings(User executor, String name, Object customSettings) {
        return FluentValidation
            .apply()
            .validate("executor", executor, NotNullValidator.apply())
            .validate("name", name, NotNullValidator.apply())
            .checkAndFail()
            .thenCompose(done -> delegate.updateCustomSettings(executor, name, customSettings));
    }

    @Override
    public CompletionStage<Done> remove(User executor, String name) {
        return FluentValidation
            .apply()
            .validate("executor", executor, NotNullValidator.apply())
            .validate("name", name, NotNullValidator.apply())
            .checkAndFail()
            .thenCompose(done -> delegate.remove(executor, name));
    }

    @Override
    public CompletionStage<Done> requestReview(User executor, String name, String message) {
        return FluentValidation
            .apply()
            .validate("executor", executor, NotNullValidator.apply())
            .validate("name", name, NotNullValidator.apply())
            .validate("message", message, NonEmptyStringValidator.apply(3))
            .checkAndFail()
            .thenCompose(done -> delegate.requestReview(executor, name, message));
    }

    @Override
    public CompletionStage<DataAccessRequestProperties> createDataAccessRequest(User executor, String name,
                                                                                String workspace, String reason) {
        return FluentValidation
            .apply()
            .validate("executor", executor, NotNullValidator.apply())
            .validate("name", name, NotNullValidator.apply())
            .validate("project", name, NotNullValidator.apply())
            .validate("reason", name, NotNullValidator.apply())
            .checkAndFail()
            .thenCompose(done -> delegate.createDataAccessRequest(executor, name, workspace, reason));
    }

    @Override
    public CompletionStage<DataAccessRequest> getDataAccessRequest(User executor, String name, UID request) {
        return FluentValidation
            .apply()
            .validate("executor", executor, NotNullValidator.apply())
            .validate("name", name, NotNullValidator.apply())
            .validate("request", request, NotNullValidator.apply())
            .checkAndFail()
            .thenCompose(done -> delegate.getDataAccessRequest(executor, name, request));
    }

    @Override
    public CompletionStage<List<DataAccessRequestProperties>> getDataAccessRequests(User executor, String name) {
        return FluentValidation
            .apply()
            .validate("executor", executor, NotNullValidator.apply())
            .validate("name", name, NotNullValidator.apply())
            .checkAndFail()
            .thenCompose(done -> delegate.getDataAccessRequests(executor, name));
    }

    @Override
    public CompletionStage<Done> approveDataAccessRequest(User executor, String name, UID request,
                                                          @Nullable String message) {
        return FluentValidation
            .apply()
            .validate("executor", executor, NotNullValidator.apply())
            .validate("name", name, NotNullValidator.apply())
            .checkAndFail()
            .thenCompose(ok -> delegate.approveDataAccessRequest(executor, name, request, message));
    }

    @Override
    public CompletionStage<Done> grantDataAccessRequest(User executor, String name, UID request,
                                                        @Nullable Instant until, @Nullable String message,
                                                        String environment, boolean downstreamApprovalRequired) {
        return FluentValidation
            .apply()
            .validate("executor", executor, NotNullValidator.apply())
            .validate("name", name, NotNullValidator.apply())
            .validate("request", name, NotNullValidator.apply())
            .checkAndFail()
            .thenCompose(done -> delegate.grantDataAccessRequest(executor, name, request, until, message, environment
                , downstreamApprovalRequired));
    }

    @Override
    public CompletionStage<Done> rejectDataAccessRequest(User executor, String name, UID request, String reason) {
        return FluentValidation
            .apply()
            .validate("executor", executor, NotNullValidator.apply())
            .validate("name", name, NotNullValidator.apply())
            .validate("request", name, NotNullValidator.apply())
            .validate("reason", name, NotNullValidator.apply())
            .checkAndFail()
            .thenCompose(done -> delegate.rejectDataAccessRequest(executor, name, request, reason));
    }

    @Override
    public CompletionStage<Done> updateDataAccessRequest(User executor, String name, UID request, String reason) {
        return FluentValidation
            .apply()
            .validate("executor", executor, NotNullValidator.apply())
            .validate("name", name, NotNullValidator.apply())
            .validate("request", name, NotNullValidator.apply())
            .validate("reason", name, NotNullValidator.apply())
            .checkAndFail()
            .thenCompose(done -> delegate.updateDataAccessRequest(executor, name, request, reason));
    }

    @Override
    public CompletionStage<Done> withdrawDataAccessRequest(User executor, String name, UID request,
                                                           @Nullable String reason) {
        return FluentValidation
            .apply()
            .validate("executor", executor, NotNullValidator.apply())
            .validate("name", name, NotNullValidator.apply())
            .validate("request", name, NotNullValidator.apply())
            .checkAndFail()
            .thenCompose(done -> delegate.withdrawDataAccessRequest(executor, name, request, reason));
    }

    @Override
    public CompletionStage<Done> grant(User executor, String name, Authorization member, DataAssetMemberRole role) {
        return FluentValidation
            .apply()
            .validate("executor", executor, NotNullValidator.apply())
            .validate("name", name, NonEmptyStringValidator.apply())
            .validate("member", member, AuthorizationValidator.apply())
            .validate("role", role, NotNullValidator.apply())
            .checkAndFail()
            .thenCompose(done -> delegate.grant(executor, name, member, role));
    }

    @Override
    public CompletionStage<Done> revoke(User executor, String name, Authorization member) {
        return FluentValidation
            .apply()
            .validate("executor", executor, NotNullValidator.apply())
            .validate("name", name, NonEmptyStringValidator.apply())
            .validate("member", member, AuthorizationValidator.apply())
            .checkAndFail()
            .thenCompose(done -> delegate.revoke(executor, name, member));
    }

    @Override
    public CompletionStage<List<Workspace>> getUsersWorkspaces(User executor) {
        return delegate.getUsersWorkspaces(executor);
    }

}
