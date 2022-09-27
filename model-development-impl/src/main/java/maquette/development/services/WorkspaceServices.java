package maquette.development.services;

import akka.Done;
import com.fasterxml.jackson.databind.JsonNode;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.user.User;
import maquette.development.values.EnvironmentType;
import maquette.development.values.Workspace;
import maquette.development.values.WorkspaceMemberRole;
import maquette.development.values.WorkspaceProperties;
import maquette.development.values.model.Model;
import maquette.development.values.model.ModelMemberRole;
import maquette.development.values.model.ModelProperties;
import maquette.development.values.model.governance.CodeIssue;
import maquette.development.values.stacks.VolumeConfiguration;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface WorkspaceServices {

    /**
     * Creates a new workspace.
     *
     * @param user    The user who wants to create the workspace.
     * @param name    The name of the workspace.
     * @param title   The title of the workspace.
     * @param summary A short summary which describes the initiative or project the workspace is used for.
     * @return Done.
     */
    CompletionStage<Done> create(User user, String name, String title, String summary);

    /**
     * Get environment variables/ properties for a workspace.
     *
     * @param user            The user who requests the environment.
     * @param workspace       The name of the workspace for which the environment should be provided.
     * @param environmentType The type of the environment.
     * @return The environment variables for the workspace.
     */
    CompletionStage<Map<String, String>> environment(User user, String workspace, EnvironmentType environmentType);

    /**
     * See {@link WorkspaceServices#environment(User, String, EnvironmentType)}. The environment type defaults to
     * {@link EnvironmentType#EXTERNAL}
     *
     * @param user      The user who requests the environment.
     * @param workspace The name of the workspace for which the environment should be provided.
     * @return The environment variables for the workspace.
     */
    default CompletionStage<Map<String, String>> environment(User user, String workspace) {
        return environment(user, workspace, EnvironmentType.EXTERNAL);
    }

    /**
     * Lists workspaces of a user. The result contains only workspaces the user has access to.
     *
     * @param user The user who requests the list.
     * @return A list of workspaces.
     */
    CompletionStage<List<WorkspaceProperties>> list(User user);

    /**
     * Get details of a workspace.
     *
     * @param user      The user who requests the details.
     * @param workspace The workspace name of the requested workspace.
     * @return Workspace details.
     */
    CompletionStage<Workspace> get(User user, String workspace);

    /**
     * Remove an existing workspace.
     *
     * @param user      The user who executes the deletion.
     * @param workspace The name of the workspace to be deleted.
     * @return Done.
     */
    CompletionStage<Done> remove(User user, String workspace);

    /**
     * Update properties of a workspace.
     *
     * @param user        The user who updates the properties.
     * @param workspace   The current name of the workspace.
     * @param updatedName The updated name of the workspace.
     * @param title       The updated title of the workspace.
     * @param summary     The updated summary of the workspace.
     * @return Done.
     */
    CompletionStage<Done> update(User user, String workspace, String updatedName, String title, String summary);

    /*
     * Models
     */

    /**
     * Lists available models of a workspace.
     *
     * @param user      The user who requests the list.
     * @param workspace The name of the workspace.
     * @return The list of models.
     */
    CompletionStage<List<ModelProperties>> getModels(User user, String workspace);

    /**
     * Get details of a model.
     *
     * @param user      The user who requests the details.
     * @param workspace The name of the workspace the model belongs to.
     * @param model     The name of the model.
     * @return Model details.
     */
    CompletionStage<Model> getModel(User user, String workspace, String model);

    /**
     * Update details and properties of a model.
     *
     * @param user        The user who updates the properties.
     * @param workspace   The name of the workspace the model belongs to.
     * @param model       The name of the model which should be updated.
     * @param title       The new title of the model.
     * @param description The new description of the model.
     * @return Done.
     */
    CompletionStage<Done> updateModel(User user, String workspace, String model, String title, String description);

    /**
     * Updates properties of a model version.
     *
     * @param user        The user who updates the properties.
     * @param workspace   The name of the workspace the model belongs to.
     * @param model       The name of the model.
     * @param version     The version of the model.
     * @param description The updated description of the version.
     * @return Done.
     */
    CompletionStage<Done> updateModelVersion(User user, String workspace, String model, String version,
                                             String description);

    /**
     * Submit responses to the model questionnaire.
     *
     * @param user      The user who submits the responses.
     * @param workspace The name of the workspace the model belongs to.
     * @param model     The name of the model.
     * @param version   The version of the model.
     * @param responses The responses of the questionnaire.
     * @return Done.
     */
    CompletionStage<Done> answerQuestionnaire(User user, String workspace, String model, String version,
                                              JsonNode responses);

    /**
     * Approve a model version for production deployment.
     *
     * @param user      The user who approves the model version.
     * @param workspace The name of the workspace to which the model belongs to.
     * @param model     The name of the model.
     * @param version   The version which should be approved.
     * @return Done.
     */
    CompletionStage<Done> approveModel(User user, String workspace, String model, String version);

    /**
     * Move (promote) a model to another (next) deployment stage.
     *
     * @param user      The user who triggers the promotion.
     * @param workspace The name of the workspace the model belongs to.
     * @param model     The name of the model.
     * @param version   The version which should be promoted.
     * @param stage     The stage to which the model should be promoted.
     * @return Done.
     */
    CompletionStage<Done> promoteModel(User user, String workspace, String model, String version, String stage);

    /**
     * Reject a model version for production usage.
     *
     * @param user      The user who rejects the model.
     * @param workspace The name of the workspace the model belongs to.
     * @param model     The name of the model.
     * @param version   The version of the model to be rejected.
     * @param reason    A message which describes why it is rejected.
     * @return Done.
     */
    CompletionStage<Done> rejectModel(User user, String workspace, String model, String version, String reason);

    /**
     * Request a review of a model version to allow production deployment.
     *
     * @param user      The user who requests the review.
     * @param workspace The name of the workspace the model belongs to.
     * @param model     The name of the model.
     * @param version   The version of the model.
     * @return Done.
     */
    CompletionStage<Done> requestModelReview(User user, String workspace, String model, String version);

    /**
     * Log/ report model quality of a model.
     *
     * @param user      The user which reports the metrics (usually a technical user).
     * @param workspace The name of the workspace the model belongs to.
     * @param model     The name of the model.
     * @param version   The version of the model.
     * @param commit    The Git commit hash of the reported metrics.
     * @param score     A score which summarizes the quality.
     * @param coverage  % of code coverage of tests.
     * @param issues    A list of issues detected.
     * @return Done.
     */
    CompletionStage<Done> reportCodeQuality(User user, String workspace, String model, String version, String commit,
                                            int score, int coverage, List<CodeIssue> issues);

    /**
     * Initiate an instance if an explainer to analyze the model.
     *
     * @param user      The user who triggers the explainer.
     * @param workspace The name of the workspace the model belongs to.
     * @param model     The name of the model.
     * @param version   The version which should be analyzed.
     * @return Done.
     */
    CompletionStage<Done> runExplainer(User user, String workspace, String model, String version);

    /**
     * Returns the questionnaire answers for the previous version of a model.
     *
     * @param user      The user who requests the answers.
     * @param workspace The name of the workspace the model belongs to.
     * @param model     The name of the model.
     * @return The latest questionnaire answers, if present.
     */
    CompletionStage<Optional<JsonNode>> getLatestQuestionnaireAnswers(User user, String workspace, String model);

    /*
     * Manage model roles
     */

    /**
     * Grant a user access to a model, or assign a user a model role.
     *
     * @param user          The user who grants the access or role.
     * @param workspace     The name of the workspace the model belongs to.
     * @param model         The name of the model.
     * @param authorization The user or group which should get the role.
     * @param role          The role which should be assigned.
     * @return Done.
     */
    CompletionStage<Done> grantModelRole(User user, String workspace, String model, UserAuthorization authorization,
                                         ModelMemberRole role);

    /**
     * Revokes a role for a model from a user or user group.
     *
     * @param user          The user who revokes the role.
     * @param workspace     The name of the workspace a model belongs to.
     * @param model         The name of the model.
     * @param authorization The user(s) which should get the roles revoked.
     * @return Done.
     */
    CompletionStage<Done> revokeModelRole(User user, String workspace, String model, UserAuthorization authorization);

    /*
     * Manage members
     */

    /**
     * Grant a member role to a user or a group of users.
     *
     * @param user          The user who grants the access role.
     * @param workspace     The name of the workspace to which a user should be assigned.
     * @param authorization The user(s) which should get the role.
     * @param role          The role which should be assigned.
     * @return Done.
     */
    CompletionStage<Done> grant(User user, String workspace, Authorization authorization, WorkspaceMemberRole role);

    /**
     * Revokes access from members.
     *
     * @param user          The user who revokes the access.
     * @param workspace     The name of the workspace for which role assignments should be revoked.
     * @param authorization The user(s) which should get their roles revoked.
     * @return Done.
     */
    CompletionStage<Done> revoke(User user, String workspace, Authorization authorization);

    /**
     * Initiate a re-deployment of the current infrastructure linked to workspaces.
     *
     * @param user The user who executes the action.
     * @return Done.
     */
    CompletionStage<Done> redeployInfrastructure(User user);

    /**
     * Provides all volumes created by a user within a workspace
     *
     * @param user      the user who executes the action
     * @param workspace the workspace
     * @return list of volumes
     */
    CompletionStage<List<VolumeConfiguration>> getVolumes(User user, String workspace);
}
