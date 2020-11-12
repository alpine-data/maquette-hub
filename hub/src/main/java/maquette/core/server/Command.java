package maquette.core.server;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.commands.*;
import maquette.core.server.commands.datasets.*;
import maquette.core.server.commands.datasets.requests.*;
import maquette.core.server.commands.datasets.tokens.CreateDatasetDataAccessTokenCommand;
import maquette.core.server.commands.datasets.tokens.ListDatasetDataAccessTokensCommand;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;


@JsonTypeInfo(
   use = JsonTypeInfo.Id.NAME,
   property = "command")
@JsonSubTypes(
   {
      // Datasets
      @JsonSubTypes.Type(value = CreateDatasetCommand.class, name = "datasets create"),
      @JsonSubTypes.Type(value = CreateDatasetDataAccessRequestCommand.class, name = "datasets access-requests create"),
      @JsonSubTypes.Type(value = GetDatasetCommand.class, name = "datasets get"),
      @JsonSubTypes.Type(value = GetDatasetDataAccessRequestCommand.class, name = "datasets access-requests get"),
      @JsonSubTypes.Type(value = GrantDatasetDataAccessRequestCommand.class, name = "datasets access-requests grant"),
      @JsonSubTypes.Type(value = ListDatasetDataAccessRequestsCommand.class, name = "datasets access-requests list"),
      @JsonSubTypes.Type(value = RejectDatasetDataAccessRequestCommand.class, name = "datasets access-requests reject"),
      @JsonSubTypes.Type(value = UpdateDatasetDataAccessRequestCommand.class, name = "datasets access-requests update"),
      @JsonSubTypes.Type(value = WithdrawDatasetDataAccessRequestCommand.class, name = "datasets access-requests withdraw"),
      @JsonSubTypes.Type(value = CreateDatasetDataAccessTokenCommand.class, name = "datasets access-tokens create"),
      @JsonSubTypes.Type(value = ListDatasetDataAccessTokensCommand.class, name = "datasets access-tokens list"),
      @JsonSubTypes.Type(value = ListDatasetsCommand.class, name = "datasets list"),
      @JsonSubTypes.Type(value = RemoveDatasetCommand.class, name = "datasets remove"),

      // Projects
      @JsonSubTypes.Type(value = CreateProjectCommand.class, name = "projects create"),
      @JsonSubTypes.Type(value = GetProjectCommand.class, name = "projects get"),
      @JsonSubTypes.Type(value = GetProjectEnvironmentCommand.class, name = "projects environment"),
      @JsonSubTypes.Type(value = ListProjectsCommand.class, name = "projects list"),
      @JsonSubTypes.Type(value = RemoveProjectCommand.class, name = "projects remove")
   })
public interface Command {

   CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services);

   Command example();

}
