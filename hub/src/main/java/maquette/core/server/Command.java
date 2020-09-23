package maquette.core.server;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.commands.CreateProjectCommand;
import maquette.core.server.commands.ListProjectsCommand;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@JsonTypeInfo(
   use = JsonTypeInfo.Id.NAME,
   property = "command")
@JsonSubTypes(
   {
      @JsonSubTypes.Type(value = CreateProjectCommand.class, name = "projects create"),
      @JsonSubTypes.Type(value = ListProjectsCommand.class, name = "projects list")
   })
public interface Command {

   CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services);

   Command example();

}
