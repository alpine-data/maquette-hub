package maquette.core.server;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.commands.*;
import maquette.core.server.commands.datasets.*;
import maquette.core.server.commands.datasets.data.CommitRevisionCommand;
import maquette.core.server.commands.datasets.data.CreateRevisionCommand;
import maquette.core.server.commands.datasets.data.ListDatasetVersionsCommand;
import maquette.core.server.commands.datasets.requests.*;
import maquette.core.server.commands.datasets.tokens.CreateDatasetDataAccessTokenCommand;
import maquette.core.server.commands.datasets.tokens.ListDatasetDataAccessTokensCommand;
import maquette.core.server.commands.projects.GrantProjectAccessCommand;
import maquette.core.server.commands.projects.RevokeProjectAccessCommand;
import maquette.core.server.commands.projects.UpdateProjectPropertiesCommand;
import maquette.core.server.commands.sandboxes.CreateSandboxCommand;
import maquette.core.server.commands.sandboxes.GetSandboxCommand;
import maquette.core.server.commands.sandboxes.GetStacksCommand;
import maquette.core.server.commands.sandboxes.ListSandboxesCommand;
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

      @JsonSubTypes.Type(value = GrantDatasetAccessCommand.class, name = "datasets grant"),
      @JsonSubTypes.Type(value = RevokeDatasetAccessCommand.class, name = "datasets revoke"),

      @JsonSubTypes.Type(value = ListDatasetsCommand.class, name = "datasets list"),
      @JsonSubTypes.Type(value = RemoveDatasetCommand.class, name = "datasets remove"),
      @JsonSubTypes.Type(value = CommitRevisionCommand.class, name = "datasets revisions commit"),
      @JsonSubTypes.Type(value = CreateRevisionCommand.class, name = "datasets revisions create"),
      @JsonSubTypes.Type(value = ListDatasetVersionsCommand.class, name = "datasets revisions list"),
      @JsonSubTypes.Type(value = UpdateDatasetPropertiesCommand.class, name = "datasets update"),

      // Projects
      @JsonSubTypes.Type(value = CreateProjectCommand.class, name = "projects create"),
      @JsonSubTypes.Type(value = GetProjectCommand.class, name = "projects get"),
      @JsonSubTypes.Type(value = GetProjectEnvironmentCommand.class, name = "projects environment"),
      @JsonSubTypes.Type(value = ListProjectsCommand.class, name = "projects list"),
      @JsonSubTypes.Type(value = RemoveProjectCommand.class, name = "projects remove"),
      @JsonSubTypes.Type(value = GrantProjectAccessCommand.class, name = "projects grant"),
      @JsonSubTypes.Type(value = RevokeProjectAccessCommand.class, name = "projects revoke"),
      @JsonSubTypes.Type(value = UpdateProjectPropertiesCommand.class, name = "projects update"),

      // Sandboxes
      @JsonSubTypes.Type(value = CreateSandboxCommand.class, name = "sandboxes create"),
      @JsonSubTypes.Type(value = GetSandboxCommand.class, name = "sandboxes get"),
      @JsonSubTypes.Type(value = GetStacksCommand.class, name = "sandboxes stacks"),
      @JsonSubTypes.Type(value = ListSandboxesCommand.class, name = "sandboxes list")
   })
public interface Command {

   CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services);

   Command example();

}
