package maquette.core.modules.users.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.modules.users.GlobalRole;
import maquette.core.modules.users.model.*;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class UserServicesSecured implements UserServices {
    private static final Logger LOG = LoggerFactory.getLogger(UserServicesSecured.class);
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
    public CompletionStage<UserSettings> getSettingsWithoutMask(User executor, UID userId) {
        return delegate
            .getProfile(executor)
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
            if ((StringUtils.equalsAnyIgnoreCase(id.getValue(), profile
                .getId()
                .getValue())) && (StringUtils.equalsAnyIgnoreCase(
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

    @Override
    public CompletionStage<List<GrantedAuthorization<GlobalRole>>> getGlobalRoles(User executor) {
        return comp
            .withAuthorization(
                () -> delegate.hasGlobalRole(executor, GlobalRole.ADMIN))
            .thenCompose(ok -> delegate.getGlobalRoles(executor));
    }

    @Override
    public CompletionStage<Done> grantGlobalRole(User executor, Authorization authorization, GlobalRole role) {
        return comp
            .withAuthorization(
                () -> delegate.hasGlobalRole(executor, GlobalRole.ADMIN))
            .thenCompose(ok -> delegate.grantGlobalRole(executor, authorization, role));
    }

    @Override
    public CompletionStage<Done> removeGlobalRole(User executor, Authorization authorization, GlobalRole role) {
        return comp
            .withAuthorization(
                () -> delegate.hasGlobalRole(executor, GlobalRole.ADMIN))
            .thenCompose(ok -> delegate.removeGlobalRole(executor, authorization, role));
    }

    @Override
    public CompletionStage<Set<GlobalRole>> getGlobalRolesForUser(User executor) {
        return delegate.getGlobalRolesForUser(executor);
    }

    @Override
    public CompletionStage<Boolean> hasGlobalRole(User executor, GlobalRole role) {
        return delegate.hasGlobalRole(executor, role);
    }
}
