package maquette.core.modules.ports;

import akka.Done;
import maquette.core.modules.users.GlobalRole;
import maquette.core.modules.users.model.UserAuthenticationToken;
import maquette.core.modules.users.model.UserNotification;
import maquette.core.modules.users.model.UserProfile;
import maquette.core.modules.users.model.UserSettings;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.GrantedAuthorization;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface UsersRepository {

    CompletionStage<Done> insertOrUpdateProfile(UserProfile profile);

    CompletionStage<Done> insertOrUpdateNotification(UID userId, UserNotification notification);

    CompletionStage<Done> insertOrUpdateSettings(UID userId, UserSettings settings);

    CompletionStage<Done> insertOrUpdateAuthenticationToken(UID userId, UserAuthenticationToken token);

    CompletionStage<List<UserProfile>> getUsers();

    CompletionStage<List<UserProfile>> getUsers(String query);

    CompletionStage<Optional<UserAuthenticationToken>> findAuthenticationTokenByUserId(UID userId);

    CompletionStage<Optional<UserAuthenticationToken>> findAuthenticationTokenByTokenId(UID tokenId);

    CompletionStage<Optional<UserNotification>> findNotificationById(UID userId, UID notificationId);

    CompletionStage<Optional<UserProfile>> findProfileById(UID userId);

    CompletionStage<Optional<UserProfile>> findProfileByAuthenticationToken(UID tokenId);

    CompletionStage<Optional<UserSettings>> findSettingsById(UID userId);

    CompletionStage<List<UserNotification>> getAllNotifications(UID userId);

    CompletionStage<List<GrantedAuthorization<GlobalRole>>> getAllGlobalAuthorizations();

    CompletionStage<Done> insertGlobalAuthorization(GrantedAuthorization<GlobalRole> globalRole);

    CompletionStage<Done> removeGlobalAuthorization(Authorization authorization, GlobalRole role);

}
