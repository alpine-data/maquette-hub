package maquette.core.modules.ports;

import akka.Done;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.modules.users.model.UserAuthenticationToken;
import maquette.core.modules.users.model.UserNotification;
import maquette.core.modules.users.model.UserProfile;
import maquette.core.modules.users.model.UserSettings;
import maquette.core.values.UID;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class InMemoryUsersRepository implements UsersRepository {

    private final List<StoredUserNotification> notifications;

    private final Map<UID, UserProfile> profiles;

    private final Map<UID, UserSettings> settings;

    private final Map<UID, UserAuthenticationToken> tokens;

    public static InMemoryUsersRepository apply() {
        return apply(Lists.newArrayList(), Maps.newHashMap(), Maps.newHashMap(), Maps.newHashMap());
    }

    @Override
    public CompletionStage<Done> insertOrUpdateProfile(UserProfile profile) {
        profiles.put(profile.getId(), profile);
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<Done> insertOrUpdateNotification(UID userId, UserNotification notification) {
        notifications
            .stream()
            .filter(n -> n.userId.equals(userId) && n.notification.getId().equals(userId))
            .forEach(notifications::remove);

        notifications.add(StoredUserNotification.apply(userId, notification));

        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<Done> insertOrUpdateSettings(UID userId, UserSettings settings) {
        this.settings.put(userId, settings);
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<Done> insertOrUpdateAuthenticationToken(UID userId, UserAuthenticationToken token) {
        this.tokens.put(userId, token);
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<List<UserProfile>> getUsers() {
        return CompletableFuture.completedFuture(new ArrayList<>(profiles.values()));
    }

    @Override
    public CompletionStage<List<UserProfile>> getUsers(String query) {
        var users = profiles
            .values()
            .stream()
            .filter(p -> p.toString().contains(query))
            .collect(Collectors.toList());

        return CompletableFuture.completedFuture(users);
    }

    @Override
    public CompletionStage<Optional<UserAuthenticationToken>> findAuthenticationTokenByUserId(UID userId) {
        if (tokens.containsKey(userId)) {
            return CompletableFuture.completedFuture(Optional.of(tokens.get(userId)));
        } else {
            return CompletableFuture.completedFuture(Optional.empty());
        }
    }

    @Override
    public CompletionStage<Optional<UserAuthenticationToken>> findAuthenticationTokenByTokenId(UID tokenId) {
        var result = tokens
            .values()
            .stream()
            .filter(token -> token.getId().equals(tokenId))
            .findFirst();

        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<Optional<UserNotification>> findNotificationById(UID userId, UID notificationId) {
        var result = notifications
            .stream()
            .filter(notification -> notification.userId.equals(userId) && notification.notification.getId()
                .equals(notificationId))
            .map(StoredUserNotification::getNotification)
            .findAny();

        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<Optional<UserProfile>> findProfileById(UID userId) {
        if (profiles.containsKey(userId)) {
            return CompletableFuture.completedFuture(Optional.of(profiles.get(userId)));
        } else {
            return CompletableFuture.completedFuture(Optional.empty());
        }
    }

    @Override
    public CompletionStage<Optional<UserProfile>> findProfileByAuthenticationToken(UID tokenId) {
        var user = tokens
            .entrySet()
            .stream()
            .filter(e -> e.getValue().getId().equals(tokenId))
            .map(Map.Entry::getKey)
            .findFirst();

        var result = user.flatMap(userId -> Optional.ofNullable(profiles.get(userId)));

        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<Optional<UserSettings>> findSettingsById(UID userId) {
        if (settings.containsKey(userId)) {
            return CompletableFuture.completedFuture(Optional.of(settings.get(userId)));
        } else {
            return CompletableFuture.completedFuture(Optional.empty());
        }
    }

    @Override
    public CompletionStage<List<UserNotification>> getAllNotifications(UID userId) {
        var result = notifications
            .stream()
            .filter(n -> n.userId.equals(userId))
            .map(StoredUserNotification::getNotification)
            .collect(Collectors.toList());

        return CompletableFuture.completedFuture(result);
    }

    @Value
    @AllArgsConstructor(staticName = "apply")
    private static class StoredUserNotification {

        UID userId;

        UserNotification notification;

    }

}
