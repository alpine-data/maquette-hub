package maquette.core.modules.users.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.modules.users.UserModule;
import maquette.core.modules.users.model.GitSettings;
import maquette.core.modules.users.model.UserProfile;
import maquette.core.modules.users.model.UserSettings;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.MessageResult;
import maquette.core.values.UID;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public final class UpdateUserCommand implements Command {

    UserProfile profile;

    UserSettings settings;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime
            .getModule(UserModule.class)
            .getServices()
            .updateUser(user, profile.getId(), profile, settings)
            .thenApply(done -> MessageResult.apply("Successfully updated user."));
    }

    @Override
    public Command example() {
        return apply(
            UserProfile.apply(UID.apply("alice"), "Alice Kaye", "Data Scientist", "Lorem ipsum dolor", "alice@mail" +
                ".con", "+49 12345 281 12", "Entenhausen"),
            UserSettings.apply(GitSettings.apply("username", "password", "privateSSHKey", "publicSSHKey")));
    }

}
