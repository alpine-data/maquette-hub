package maquette.core.server;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.commands.CreateProjectCommand;
import maquette.core.server.commands.GetProjectEnvironmentCommand;
import maquette.core.server.commands.ListProjectsCommand;
import maquette.core.server.commands.RemoveProjectCommand;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;


@JsonTypeInfo(
   use = JsonTypeInfo.Id.NAME,
   property = "command")
@JsonSubTypes(
   {
      @JsonSubTypes.Type(value = CreateProjectCommand.class, name = "projects create"),
      @JsonSubTypes.Type(value = GetProjectEnvironmentCommand.class, name = "projects environment"),
      @JsonSubTypes.Type(value = ListProjectsCommand.class, name = "projects list"),
      @JsonSubTypes.Type(value = RemoveProjectCommand.class, name = "projects remove")
   })
public interface Command {

   CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services);

   Command example();

}
