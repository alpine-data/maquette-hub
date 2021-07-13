package maquette.core.server.commands;

import maquette.core.MaquetteRuntime;
import maquette.core.modules.MaquetteModule;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

public interface Command {

   CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime);

   Command example();

}
