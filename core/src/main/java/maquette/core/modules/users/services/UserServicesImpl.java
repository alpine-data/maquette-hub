package maquette.core.modules.users.services;

import akka.Done;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.modules.users.GlobalRole;
import maquette.core.modules.users.UserEntities;
import maquette.core.modules.users.UserEntity;
import maquette.core.modules.users.model.UserAuthenticationToken;
import maquette.core.modules.users.model.UserNotification;
import maquette.core.modules.users.model.UserProfile;
import maquette.core.modules.users.model.UserSettings;
import maquette.core.values.UID;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class UserServicesImpl implements UserServices {

    private final UserEntities users;

    private final UserCompanion companion;

    /*
     * Notifications
     */

    @Override
    public CompletionStage<UserAuthenticationToken> getAuthenticationToken(User executor) {
        if (executor instanceof AuthenticatedUser) {
            return companion
                .withUser(executor)
                .thenCompose(UserEntity::getAuthenticationToken);
        } else {
            return CompletableFuture.completedFuture(UserAuthenticationToken.apply(UID.apply("0815"), "0815",
                Instant.MAX));
        }
    }

    @Override
    public CompletionStage<AuthenticatedUser> getUserForAuthenticationToken(String tokenId, String tokenSecret) {
        return users.getUserForAuthenticationToken(UID.apply(tokenId), tokenSecret);
    }

    @Override
    public CompletionStage<Done> registerAuthenticationToken(User executor, String randomId) {
        return users.registerAuthenticationToken(executor, randomId);
    }

    @Override
    public CompletionStage<UserAuthenticationToken> getAuthenticationToken(String randomId) {
        return users.readAuthenticationToken(randomId);
    }

    @Override
    public CompletionStage<UserProfile> getProfile(User executor, UID userId) {
        return companion
            .withUser(userId)
            .thenCompose(UserEntity::getProfileById);
    }

    @Override
    public CompletionStage<UserProfile> getProfile(User executor) {
        return companion
            .withUser(executor)
            .thenCompose(UserEntity::getProfileById);
    }


    @Override
    public CompletionStage<UserSettings> getSettingsWithoutMask(User executor, UID userId) {
        return companion
            .withUser(userId)
            .thenCompose(entity -> entity.getSettings(false));
    }

    @Override
    public CompletionStage<UserSettings> getSettings(User executor, UID userId) {
        return companion
            .withUser(userId)
            .thenCompose(entity -> entity.getSettings(true));
    }

    @Override
    public CompletionStage<List<UserProfile>> getUsers(User executor) {
        return getUsers(executor, "");
    }

    @Override
    public CompletionStage<List<UserProfile>> getUsers(User executor, String query) {
        return users.getUsers(query);
    }

    @Override
    public CompletionStage<Map<String, UserProfile>> getUsers(User executor, List<UID> userIds) {
        return Operators
            .allOf(userIds
                .stream()
                .map(id -> getProfile(executor, id)))
            .thenApply(users -> {
                var result = Maps.<String, UserProfile>newHashMap();
                users.forEach(user -> result.put(user
                    .getId()
                    .getValue(), user));
                return result;
            });
    }

    @Override
    public CompletionStage<Done> updateUserDetails(User executor, String base64encodedDetails) {
        return companion
            .withUser(executor)
            .thenCompose(entity -> entity.updateUserDetails(base64encodedDetails));
    }

    @Override
    public CompletionStage<Done> updateUser(User executor, UID userId, UserProfile profile, UserSettings settings) {
        return companion
            .withUser(userId)
            .thenCompose(entity -> entity
                .updateUserProfile(profile)
                .thenApply(d -> entity)
                .thenApply(b -> b.createNewNotification("User info Updated."))
                .thenApply(d -> entity))
            .thenCompose(entity -> entity.updateUserSettings(settings));
    }

    @Override
    public CompletionStage<Done> createUser(User executor, UID userId, UserProfile profile) {
        return companion
            .withUser(userId)
            .thenCompose(entity -> entity.updateUserProfile(profile));
    }

    @Override
    public CompletionStage<List<UserNotification>> getNotifications(User executor) {
        return companion.withUserOrDefault(executor, Lists.newArrayList(), UserEntity::getNotifications);
    }

    @Override
    public CompletionStage<Done> readNotification(User executor, String notificationId) {
        return companion.withUserOrDefault(executor, Done.getInstance(), user -> user.readNotification(notificationId));
    }

    @Override
    public CompletionStage<Set<GlobalRole>> getGlobalRoles(User executor) {
        return users.getGlobalRoles(executor);
    }

    @Override
    public CompletionStage<Boolean> hasGlobalRole(User executor, GlobalRole role) {
        return users.hasGlobalRole(executor, role);
    }

}
