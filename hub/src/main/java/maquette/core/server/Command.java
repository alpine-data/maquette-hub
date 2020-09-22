package maquette.core.server;

import maquette.core.config.RuntimeConfiguration;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

public interface Command {

    CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services);

}
