package maquette.core.modules.users;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import maquette.core.modules.ports.UsersRepository;
import maquette.core.modules.users.exceptions.MissingGitSettings;
import maquette.core.modules.users.model.*;
import maquette.core.values.UID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class UserEntity {

    private static final String SECRET_MASK = "__secret__";

    private static final Logger LOG = LoggerFactory.getLogger(UserEntity.class);

    private final UID id;

    private final UsersRepository repository;

    private final ObjectMapper om;

    public CompletionStage<Done> createNewNotification(String message) {
        var notification = UserNotification.apply(UID.apply(), Instant.now(), false, message);
        return repository.insertOrUpdateNotification(id, notification);
    }

    public CompletionStage<Done> updateUserDetails(String base64encodedUserDetails) {
        try {
            var json = new String(Base64.getDecoder().decode(base64encodedUserDetails), StandardCharsets.UTF_8);
            var update = om.readValue(json, UserDetails.class);

            return repository
                .findProfileById(id)
                .thenApply(profile -> profile.orElse(UserProfile.apply(id, "", "", "", "", "", "")))
                .thenApply(profile -> profile
                    .withEmail(update.getEmail())
                    .withName(update.getName()))
                .thenCompose(repository::insertOrUpdateProfile);
        } catch (IOException ex) {
            LOG.warn("An error occurred while updating user details for user `{}`", id, ex);
        }

        return CompletableFuture.completedFuture(Done.getInstance());
    }

    public CompletionStage<Done> updateUserProfile(UserProfile profile) {
        return repository.insertOrUpdateProfile(profile.withId(id));
    }

    public CompletionStage<Done> updateUserSettings(UserSettings settings) {
        return repository
            .findSettingsById(id)
            .thenApply(maybeSettings -> {
                if (maybeSettings.isEmpty()) {
                    return UserSettings.apply(null);
                } else {
                    return maybeSettings.get();
                }
            })
            .thenApply(current -> {
                var updatedSettings = current;

                if (settings.getGit().isPresent()) {
                    var updated = settings.getGit().get();

                    if (updated.getPassword() != null && updated.getPassword().equals(SECRET_MASK)) {
                        updated = updated
                            .withPassword(current.getGit().map(GitSettings::getPassword).orElse(""));
                    }

                    if (updated.getPrivateKey() != null && updated.getPrivateKey().equals(SECRET_MASK)) {
                        updated = updated
                            .withPrivateKey(current.getGit().map(GitSettings::getPrivateKey).orElse(""));
                    }


                    if (updated.getPublicKey() != null && updated.getPublicKey().equals(SECRET_MASK)) {
                        updated = updated
                            .withPrivateKey(current.getGit().map(GitSettings::getPublicKey).orElse(""));
                    }

                    if (updated.isEmpty()) {
                        updatedSettings = updatedSettings.withGit(null);
                    } else {
                        updatedSettings = updatedSettings.withGit(updated);
                    }
                }

                return updatedSettings;
            })
            .thenCompose(updatedSettings -> repository.insertOrUpdateSettings(id, updatedSettings));
    }

    public CompletionStage<UserAuthenticationToken> getAuthenticationToken() {
        return repository
            .findAuthenticationTokenByUserId(id)
            .thenCompose(maybeToken -> {
                if (maybeToken.isPresent() && maybeToken.get().getValidBefore().isAfter(Instant.now())) {
                    return CompletableFuture.completedFuture(maybeToken.get());
                } else {
                    var id = UID.apply();
                    var secret = UUID.randomUUID().toString();
                    var auth = UserAuthenticationToken.apply(id, secret, Instant.now().plusSeconds(60 * 60 * 24));

                    return repository.insertOrUpdateAuthenticationToken(this.id, auth).thenApply(d -> auth);
                }
            });
    }

    public CompletionStage<UserProfile> getProfile() {
        return repository
            .findProfileById(id)
            .thenApply(profile -> profile.orElse(UserProfile.apply(id, "", "", "", "", "", "")));
    }

    public CompletionStage<UserSettings> getSettings() {
        return getSettings(true);
    }

    public CompletionStage<UserSettings> getSettings(boolean masked) {
        return repository
            .findSettingsById(id)
            .thenApply(settings -> settings.orElse(UserSettings.apply(null)))
            .thenApply(settings -> {
                if (masked) {
                    return settings.withGit(settings
                        .getGit()
                        .map(g -> g
                            .withPassword(SECRET_MASK)
                            .withPrivateKey(SECRET_MASK)
                            .withPublicKey(SECRET_MASK))
                        .orElse(null));
                } else {
                    return settings;
                }
            });
    }

    public CompletionStage<GitSettings> getGitSettings() {
        return repository
            .findSettingsById(id)
            .thenApply(settings -> settings.flatMap(UserSettings::getGit))
            .thenApply(optGitSettings -> optGitSettings.orElseThrow(MissingGitSettings::apply));
    }

    public CompletionStage<List<UserNotification>> getNotifications() {
        return repository.getAllNotifications(id);
    }

    public CompletionStage<Done> readNotification(String notificationId) {
        return repository
            .findNotificationById(id, UID.apply(notificationId))
            .thenCompose(maybeNotification -> {
                if (maybeNotification.isPresent()) {
                    var notification = maybeNotification.get().withRead(true);
                    return repository.insertOrUpdateNotification(id, notification);
                } else {
                    return CompletableFuture.completedFuture(Done.getInstance());
                }
            });
    }

}
