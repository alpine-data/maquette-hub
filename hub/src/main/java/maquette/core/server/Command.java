package maquette.core.server;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.commands.data.datasets.*;
import maquette.core.server.commands.data.datasets.revisions.*;
import maquette.core.server.commands.data.datasets.requests.*;
import maquette.core.server.commands.data.datasets.members.*;
import maquette.core.server.commands.data.datasets.tokens.*;
import maquette.core.server.commands.data.datasources.*;
import maquette.core.server.commands.data.datasources.members.GrantDataSourceMemberCommand;
import maquette.core.server.commands.data.datasources.members.RevokeDataSourceMemberCommand;
import maquette.core.server.commands.data.datasources.requests.*;
import maquette.core.server.commands.projects.*;
import maquette.core.server.commands.sandboxes.CreateSandboxCommand;
import maquette.core.server.commands.sandboxes.GetSandboxCommand;
import maquette.core.server.commands.sandboxes.GetStacksCommand;
import maquette.core.server.commands.sandboxes.ListSandboxesCommand;
import maquette.core.server.commands.views.*;
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
      @JsonSubTypes.Type(value = RejectDatasetDataAccessRequestCommand.class, name = "datasets access-requests reject"),
      @JsonSubTypes.Type(value = UpdateDatasetDataAccessRequestCommand.class, name = "datasets access-requests update"),
      @JsonSubTypes.Type(value = WithdrawDatasetDataAccessRequestCommand.class, name = "datasets access-requests withdraw"),
      @JsonSubTypes.Type(value = CreateDatasetDataAccessTokenCommand.class, name = "datasets access-tokens create"),
      @JsonSubTypes.Type(value = ListDatasetDataAccessTokensCommand.class, name = "datasets access-tokens list"),

      @JsonSubTypes.Type(value = GrantDatasetMemberCommand.class, name = "datasets grant"),
      @JsonSubTypes.Type(value = RevokeDatasetMemberCommand.class, name = "datasets revoke"),

      @JsonSubTypes.Type(value = ListDatasetsCommand.class, name = "datasets list"),
      @JsonSubTypes.Type(value = RemoveDatasetCommand.class, name = "datasets remove"),
      @JsonSubTypes.Type(value = CommitRevisionCommand.class, name = "datasets revisions commit"),
      @JsonSubTypes.Type(value = CreateRevisionCommand.class, name = "datasets revisions create"),
      @JsonSubTypes.Type(value = UpdateDatasetPropertiesCommand.class, name = "datasets update"),

      // Data Sources
      @JsonSubTypes.Type(value = CreateDataSourceCommand.class, name = "sources create"),
      @JsonSubTypes.Type(value = GetDataSourceCommand.class, name = "sources get"),
      @JsonSubTypes.Type(value = ListDataSourcesCommand.class, name = "sources list"),
      @JsonSubTypes.Type(value = RemoveDataSourceCommand.class, name = "sources remove"),
      @JsonSubTypes.Type(value = UpdateDataSourcePropertiesCommand.class, name = "sources update"),
      @JsonSubTypes.Type(value = UpdateDataSourceDatabasePropertiesCommand.class, name = "sources updated db"),

      @JsonSubTypes.Type(value = GrantDataSourceMemberCommand.class, name = "sources grant"),
      @JsonSubTypes.Type(value = RevokeDataSourceMemberCommand.class, name = "sources revoke"),

      @JsonSubTypes.Type(value = CreateDataSourceDataAccessRequestCommand.class, name = "sources access-requests create"),
      @JsonSubTypes.Type(value = GetDataSourceDataAccessRequestCommand.class, name = "sources access-requests get"),
      @JsonSubTypes.Type(value = GrantDataSourceDataAccessRequestCommand.class, name = "sources access-requests grant"),
      @JsonSubTypes.Type(value = RejectDataSourceDataAccessRequestCommand.class, name = "sources access-requests reject"),
      @JsonSubTypes.Type(value = UpdateDataSourceDataAccessRequestCommand.class, name = "sources access-requests update"),
      @JsonSubTypes.Type(value = WithdrawDataSourceDataAccessRequestCommand.class, name = "sources access-requests withdraw"),

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
      @JsonSubTypes.Type(value = ListSandboxesCommand.class, name = "sandboxes list"),

      // Views
      @JsonSubTypes.Type(value = CreateDataAccessRequestViewCommand.class, name = "views create-data-access-request"),
      @JsonSubTypes.Type(value = CreateSandboxViewCommand.class, name = "views create-sandbox"),
      @JsonSubTypes.Type(value = DashboardViewCommand.class, name = "views dashboard"),
      @JsonSubTypes.Type(value = DatasetViewCommand.class, name = "views dataset"),
      @JsonSubTypes.Type(value = ProjectViewCommand.class, name = "views project"),
      @JsonSubTypes.Type(value = SandboxViewCommand.class, name = "views sandbox"),
      @JsonSubTypes.Type(value = DataShopViewCommand.class, name = "views shop")
   })
public interface Command {

   CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services);

   Command example();

}
