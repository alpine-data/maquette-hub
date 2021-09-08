package maquette.core.server.commands;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import maquette.core.MaquetteRuntime;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "command")
public interface Command {

    CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime);

    Command example();

}
