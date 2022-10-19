package maquette.core.modules.users.services;

import akka.Done;
import maquette.core.modules.users.GlobalRole;
import maquette.core.modules.users.model.UserAuthenticationToken;
import maquette.core.modules.users.model.UserNotification;
import maquette.core.modules.users.model.UserProfile;
import maquette.core.modules.users.model.UserSettings;
import maquette.core.values.UID;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionStage;

public interface UserServices {

    /*
     * Authentication tokens
     */
    CompletionStage<UserAuthenticationToken> getAuthenticationToken(User executor);

    CompletionStage<AuthenticatedUser> getUserForAuthenticationToken(String tokenId, String tokenSecret);


    /*
     * CLI authentication
     */
    CompletionStage<Done> registerAuthenticationToken(User executor, String randomId);

    CompletionStage<UserAuthenticationToken> getAuthenticationToken(String randomId);

    /*
     * Profile
     */
    CompletionStage<UserProfile> getProfile(User executor, UID userId);

    CompletionStage<UserProfile> getProfile(User executor);


    CompletionStage<UserSettings> getSettingsWithoutMask(User executor, UID userId);

    CompletionStage<UserSettings> getSettings(User executor, UID userId);

    CompletionStage<List<UserProfile>> getUsers(User executor);

    CompletionStage<List<UserProfile>> getUsers(User executor, String query);

    CompletionStage<Map<String, UserProfile>> getUsers(User executor, List<UID> userIds);

    CompletionStage<Done> updateUserDetails(User executor, String base64encodedDetails);

    CompletionStage<Done> updateUser(User executor, UID userId, UserProfile profile, UserSettings settings);

    CompletionStage<Done> createUser(User executor, UID userId, UserProfile profile);

    /*
     * Notifications
     */
    CompletionStage<List<UserNotification>> getNotifications(User executor);

    CompletionStage<Done> readNotification(User executor, String notificationId);

    /*
     * Global Roles
     */
    CompletionStage<Set<GlobalRole>> getGlobalRoles(User executor);

    CompletionStage<Boolean> hasGlobalRole(User executor, GlobalRole role);

}
