package maquette.core.modules.users.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.modules.users.model.*;
import maquette.core.values.UID;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public class UserServicesSecured implements UserServices {

    private final UserCompanion comp;

    private final UserServices delegate;

    @Override
    public CompletionStage<UserAuthenticationToken> getAuthenticationToken(User executor) {
        return delegate.getAuthenticationToken(executor);
    }

    @Override
    public CompletionStage<AuthenticatedUser> getUserForAuthenticationToken(String tokenId, String tokenSecret) {
        return delegate.getUserForAuthenticationToken(tokenId, tokenSecret);
    }

    @Override
    public CompletionStage<Done> registerAuthenticationToken(User executor, String randomId) {
        return delegate.registerAuthenticationToken(executor, randomId);
    }

    @Override
    public CompletionStage<UserAuthenticationToken> getAuthenticationToken(String randomId) {
        return delegate.getAuthenticationToken(randomId);
    }

    @Override
    public CompletionStage<UserProfile> getProfile(User executor, UID userId) {
        return delegate.getProfile(executor, userId);
    }

    @Override
    public CompletionStage<UserProfile> getProfile(User executor) {
        return delegate.getProfile(executor);
    }

    @Override
    public CompletionStage<UserProfile> getProfileBySub(User executor) {
        return delegate.getProfileBySub(executor);
    }

    @Override
    public CompletionStage<UserSettings> getSettingsWithoutMask(User executor, UID userId) {
        return delegate
            .getProfileBySub(executor)
            .thenApply(x -> {

                if (StringUtils.equalsAnyIgnoreCase(x
                    .getId()
                    .getValue(), userId.getValue())) {
                    return Optional.of(true);
                }
                return Optional.of(false);
            })
            .thenCompose(x -> {
                if (x.get()) {
                    return delegate.getSettingsWithoutMask(executor, userId);
                } else {
                    return CompletableFuture.completedFuture(UserSettings.apply(GitSettings.apply("", "", "", "")));
                }
            });
    }

    @Override
    public CompletionStage<UserSettings> getSettings(User executor, UID userId) {
        return delegate.getSettings(executor, userId);
    }

    @Override
    public CompletionStage<List<UserProfile>> getUsers(User executor) {
        return delegate.getUsers(executor);
    }

    @Override
    public CompletionStage<List<UserProfile>> getUsers(User executor, String query) {
        return delegate.getUsers(executor, query);
    }

    @Override
    public CompletionStage<Map<String, UserProfile>> getUsers(User executor, List<UID> userIds) {
        return delegate.getUsers(executor, userIds);
    }

    @Override
    public CompletionStage<Done> updateUserDetails(User executor, String base64encodedDetails) {
        return delegate.updateUserDetails(executor, base64encodedDetails);
    }

    @Override
    public CompletionStage<Done> updateUser(User executor, UID userId, UserProfile profile, UserSettings settings) {

        if (executor instanceof AuthenticatedUser) {
            var id = ((AuthenticatedUser) executor).getId();

            if ((StringUtils.equalsAnyIgnoreCase(id.getValue(), profile.getSub())) && (StringUtils.equalsAnyIgnoreCase(
                userId.getValue(), profile
                    .getId()
                    .getValue()))) {
                return delegate.updateUser(executor, userId, profile, settings);
            }
        }
        throw new RuntimeException("Profile cannot be edited");
    }

    @Override
    public CompletionStage<Done> createUser(User executor, UID userId, UserProfile profile) {
        return delegate.createUser(executor, userId, profile);
    }

    @Override
    public CompletionStage<List<UserNotification>> getNotifications(User executor) {
        return delegate.getNotifications(executor);
    }

    @Override
    public CompletionStage<Done> readNotification(User executor, String notificationId) {
        return delegate.readNotification(executor, notificationId);
    }
}
