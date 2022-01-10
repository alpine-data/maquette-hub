package maquette.core.modules.users.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.common.validation.api.FluentValidation;
import maquette.core.common.validation.validators.ForAllValidator;
import maquette.core.common.validation.validators.NonEmptyStringValidator;
import maquette.core.common.validation.validators.NotNullValidator;
import maquette.core.modules.users.model.UserAuthenticationToken;
import maquette.core.modules.users.model.UserNotification;
import maquette.core.modules.users.model.UserProfile;
import maquette.core.modules.users.model.UserSettings;
import maquette.core.values.UID;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class UserServicesValidated implements UserServices {

    private final UserServices delegate;

    @Override
    public CompletionStage<UserAuthenticationToken> getAuthenticationToken(User executor) {
        return delegate.getAuthenticationToken(executor);
    }

    @Override
    public CompletionStage<AuthenticatedUser> getUserForAuthenticationToken(String tokenId, String tokenSecret) {
        return FluentValidation
            .apply()
            .validate("tokenId", tokenId, NotNullValidator.apply())
            .validate("tokenSecret", tokenSecret, NotNullValidator.apply())
            .checkAndFail()
            .thenCompose(done -> delegate.getUserForAuthenticationToken(tokenId, tokenSecret));
    }

    @Override
    public CompletionStage<Done> registerAuthenticationToken(User executor, String randomId) {
        return FluentValidation
            .apply()
            .validate("randomId", randomId, NotNullValidator.apply())
            .checkAndFail()
            .thenCompose(done -> delegate.registerAuthenticationToken(executor, randomId));
    }

    @Override
    public CompletionStage<UserAuthenticationToken> getAuthenticationToken(String randomId) {
        return FluentValidation
            .apply()
            .validate("randomId", randomId, NotNullValidator.apply())
            .checkAndFail()
            .thenCompose(done -> delegate.getAuthenticationToken(randomId));
    }

    @Override
    public CompletionStage<UserProfile> getProfile(User executor, UID userId) {
        return FluentValidation
            .apply()
            .validate("userId", userId, NotNullValidator.apply())
            .checkAndFail()
            .thenCompose(done -> delegate.getProfile(executor, userId));
    }

    @Override
    public CompletionStage<UserProfile> getProfile(User executor) {
        return delegate.getProfile(executor);
    }

    @Override
    public CompletionStage<UserSettings> getSettings(User executor, UID userId) {
        return FluentValidation
            .apply()
            .validate("userId", userId, NotNullValidator.apply())
            .checkAndFail()
            .thenCompose(done -> delegate.getSettings(executor, userId));
    }

    @Override
    public CompletionStage<List<UserProfile>> getUsers(User executor) {
        return delegate.getUsers(executor);
    }

    @Override
    public CompletionStage<List<UserProfile>> getUsers(User executor, String query) {
        return FluentValidation
            .apply()
            .validate("query", query, NotNullValidator.apply())
            .checkAndFail()
            .thenCompose(done -> delegate.getUsers(executor, query));
    }

    @Override
    public CompletionStage<Map<String, UserProfile>> getUsers(User executor, List<UID> userIds) {
        return FluentValidation
            .apply()
            .validate("userIds", userIds, NotNullValidator.apply())
            .validate("userIds", userIds, ForAllValidator.apply(NotNullValidator.apply()))
            .checkAndFail()
            .thenCompose(done -> delegate.getUsers(executor, userIds));
    }

    @Override
    public CompletionStage<Done> updateUserDetails(User executor, String base64encodedDetails) {
        return FluentValidation
            .apply()
            .validate("base64encodedDetails", base64encodedDetails, NotNullValidator.apply())
            .checkAndFail()
            .thenCompose(done -> delegate.updateUserDetails(executor, base64encodedDetails));
    }

    @Override
    public CompletionStage<Done> updateUser(User executor, UID userId, UserProfile profile, UserSettings settings) {
        return FluentValidation
            .apply()
            .validate("userId", userId, NotNullValidator.apply())
            .validate("profile", userId, NotNullValidator.apply())
            .validate("settings", settings, NotNullValidator.apply())
            .checkAndFail()
            .thenCompose(done -> delegate.updateUser(executor, userId, profile, settings));
    }

    @Override
    public CompletionStage<List<UserNotification>> getNotifications(User executor) {
        return delegate.getNotifications(executor);
    }

    @Override
    public CompletionStage<Done> readNotification(User executor, String notificationId) {
        return FluentValidation
            .apply()
            .validate("notificationId", notificationId, NonEmptyStringValidator.apply(1))
            .checkAndFail()
            .thenCompose(done -> delegate.readNotification(executor, notificationId));
    }
}
