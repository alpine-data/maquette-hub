package maquette.core.server;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.commands.data.*;
import maquette.core.server.commands.data.members.GrantDataAssetMemberCommand;
import maquette.core.server.commands.data.members.RevokeDataAssetMemberCommand;
import maquette.core.server.commands.data.requests.*;
import maquette.core.server.commands.dependencies.*;
import maquette.core.server.commands.projects.*;
import maquette.core.server.commands.projects.applications.CreateApplicationCommand;
import maquette.core.server.commands.projects.applications.ListApplicationsCommand;
import maquette.core.server.commands.projects.applications.RemoveApplicationCommand;
import maquette.core.server.commands.projects.models.*;
import maquette.core.server.commands.sandboxes.CreateSandboxCommand;
import maquette.core.server.commands.sandboxes.GetSandboxCommand;
import maquette.core.server.commands.sandboxes.GetStacksCommand;
import maquette.core.server.commands.sandboxes.ListSandboxesCommand;
import maquette.core.server.commands.user.UpdateUserCommand;
import maquette.core.server.commands.views.*;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;


@JsonTypeInfo(
   use = JsonTypeInfo.Id.NAME,
   property = "command")
@JsonSubTypes(
   {
      // Dependencies
      @JsonSubTypes.Type(value = GetDataAssetDependencyGraph.class, name = "dependencies get data-asset"),
      @JsonSubTypes.Type(value = TrackConsumptionByApplicationCommand.class, name = "dependencies track consumption app"),
      @JsonSubTypes.Type(value = TrackConsumptionByModelCommand.class, name = "dependencies track consumption model"),
      @JsonSubTypes.Type(value = TrackConsumptionByProjectCommand.class, name = "dependencies track consumption project"),
      @JsonSubTypes.Type(value = TrackModelUsageByApplicationCommand.class, name = "dependencies track usage app"),
      @JsonSubTypes.Type(value = TrackProductionByApplicationCommand.class, name = "dependencies track production app"),
      @JsonSubTypes.Type(value = TrackProductionByProjectCommand.class, name = "dependencies track production project"),
      @JsonSubTypes.Type(value = TrackProductionByUserCommand.class, name = "dependencies track production user"),

      // Data Assets
      @JsonSubTypes.Type(value = ApproveDataAssetCommand.class, name = "data-assets approve"),
      @JsonSubTypes.Type(value = CreateDataAssetCommand.class, name = "data-assets create"),
      @JsonSubTypes.Type(value = DeprecateDataAssetCommand.class, name = "data-assets deprecate"),
      @JsonSubTypes.Type(value = GetDataAssetCommand.class, name = "data-assets get"),
      @JsonSubTypes.Type(value = ListDataAssetsCommand.class, name = "data-assets list"),
      @JsonSubTypes.Type(value = RemoveDataAssetCommand.class, name = "data-assets remove"),
      @JsonSubTypes.Type(value = UpdateCustomDataAssetSettingsCommand.class, name = "data-assets update-custom"),
      @JsonSubTypes.Type(value = UpdateDataAssetCommand.class, name = "data-assets update"),

      @JsonSubTypes.Type(value = GrantDataAssetMemberCommand.class, name = "data-assets members grant"),
      @JsonSubTypes.Type(value = RevokeDataAssetMemberCommand.class, name = "data-assets members revoke"),

      @JsonSubTypes.Type(value = CreateAccessRequestCommand.class, name = "data-assets access-requests create"),
      @JsonSubTypes.Type(value = GetAccessRequestCommand.class, name = "data-assets access-requests get"),
      @JsonSubTypes.Type(value = GrantAccessRequestCommand.class, name = "data-assets access-requests grant"),
      @JsonSubTypes.Type(value = RejectAccessRequestCommand.class, name = "data-assets access-requests reject"),
      @JsonSubTypes.Type(value = UpdateAccessRequestCommand.class, name = "data-assets access-requests update"),
      @JsonSubTypes.Type(value = WithdrawAccessRequestCommand.class, name = "data-assets access-requests withdraw"),

      // Projects
      @JsonSubTypes.Type(value = CreateApplicationCommand.class, name = "projects applications create"),
      @JsonSubTypes.Type(value = CreateProjectCommand.class, name = "projects create"),
      @JsonSubTypes.Type(value = GetProjectCommand.class, name = "projects get"),
      @JsonSubTypes.Type(value = GetProjectEnvironmentCommand.class, name = "projects environment"),
      @JsonSubTypes.Type(value = ListApplicationsCommand.class, name = "projects applications list"),
      @JsonSubTypes.Type(value = ListProjectsCommand.class, name = "projects list"),
      @JsonSubTypes.Type(value = RemoveProjectCommand.class, name = "projects remove"),
      @JsonSubTypes.Type(value = GrantProjectAccessCommand.class, name = "projects grant"),
      @JsonSubTypes.Type(value = RemoveApplicationCommand.class, name = "projects applications remove"),
      @JsonSubTypes.Type(value = RevokeProjectAccessCommand.class, name = "projects revoke"),
      @JsonSubTypes.Type(value = UpdateProjectPropertiesCommand.class, name = "projects update"),

      @JsonSubTypes.Type(value = ListWorkspaceGeneratorsCommand.class, name = "workspaces generators list"),

      // Models
      @JsonSubTypes.Type(value = AnswerModelQuestionnaireCommand.class, name = "projects models answer-questionnaire"),
      @JsonSubTypes.Type(value = ApproveModelCommand.class, name = "projects models approve"),
      @JsonSubTypes.Type(value = GetModelCommand.class, name = "projects models get"),
      @JsonSubTypes.Type(value = GetModelsCommand.class, name = "projects models list"),
      @JsonSubTypes.Type(value = GrantModelRoleCommand.class, name = "projects models grant"),
      @JsonSubTypes.Type(value = PromoteModelCommand.class, name = "projects models promote"),
      @JsonSubTypes.Type(value = RejectModelCommand.class, name = "projects models reject"),
      @JsonSubTypes.Type(value = ReportModelCodeQualityCommand.class, name = "projects models report-cq"),
      @JsonSubTypes.Type(value = RevokeModelRoleCommand.class, name = "projects models revoke"),
      @JsonSubTypes.Type(value = RequestModelReviewCommand.class, name = "projects models request-review"),
      @JsonSubTypes.Type(value = RunExplainerCommand.class, name = "projects models run-explainer"),
      @JsonSubTypes.Type(value = UpdateModelCommand.class, name = "projects models update"),
      @JsonSubTypes.Type(value = UpdateModelVersionCommand.class, name = "projects models update-version"),

      // Sandboxes
      @JsonSubTypes.Type(value = CreateSandboxCommand.class, name = "sandboxes create"),
      @JsonSubTypes.Type(value = GetSandboxCommand.class, name = "sandboxes get"),
      @JsonSubTypes.Type(value = GetStacksCommand.class, name = "sandboxes stacks"),
      @JsonSubTypes.Type(value = ListSandboxesCommand.class, name = "sandboxes list"),

      // User
      @JsonSubTypes.Type(value = UpdateUserCommand.class, name = "users update"),

      // Views
      @JsonSubTypes.Type(value = DataAssetViewCommand.class, name = "views asset"),
      @JsonSubTypes.Type(value = CreateDataAccessRequestViewCommand.class, name = "views create-data-access-request"),
      @JsonSubTypes.Type(value = CreateDataAssetViewCommand.class, name = "views data-asset create"),
      @JsonSubTypes.Type(value = CreateSandboxViewCommand.class, name = "views create-sandbox"),
      @JsonSubTypes.Type(value = DashboardViewCommand.class, name = "views dashboard"),
      @JsonSubTypes.Type(value = ProjectViewCommand.class, name = "views project"),
      @JsonSubTypes.Type(value = SandboxViewCommand.class, name = "views sandbox"),
      @JsonSubTypes.Type(value = DataShopViewCommand.class, name = "views shop"),
      @JsonSubTypes.Type(value = UserProfileViewCommand.class, name = "views user"),
      @JsonSubTypes.Type(value = UserSettingsViewCommand.class, name = "views user settings")
   })
public interface Command {

   CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services);

   Command example();

}
