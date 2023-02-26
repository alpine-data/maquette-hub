package maquette.development.services;

import akka.Done;
import maquette.core.MaquetteRuntime;
import maquette.core.modules.applications.model.Application;
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
import maquette.development.values.model.ModelVersionStage;
import maquette.development.values.model.governance.CodeIssue;
import maquette.development.values.model.services.ModelServiceProperties;
import maquette.development.values.stacks.VolumeProperties;

import java.util.List;
import java.util.Map;
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
     * Creates a service to serve the model as an API.
     * This will create a new Git repository including the required code to serve a model and the DevOps Pipeline
     * to deploy the service.
     *
     * @param user      The user who executes the action.
     * @param workspace The name of the workspace the model belongs to.
     * @param model     The name of the model.
     * @param version   The version of the model.
     * @param service   The name of the service to create.
     * @return ModelServiceProperties which contain links to the created service.
     */
    CompletionStage<ModelServiceProperties> createModelService(User user, String workspace, String model,
                                                               String version, String service);

    /**
     * Get environment variables/ properties for a workspace.
     *
     * @param user            The user who requests the environment.
     * @param workspace       The name of the workspace for which the environment should be provided.
     * @param environmentType The type of the environment.
     * @return The environment variables for the workspace.
     */
    CompletionStage<Map<String, String>> getEnvironment(User user, String workspace, EnvironmentType environmentType);

    /**
     * See {@link WorkspaceServices#getEnvironment(User, String, EnvironmentType)}. The environment type defaults to
     * {@link EnvironmentType#EXTERNAL}
     *
     * @param user      The user who requests the environment.
     * @param workspace The name of the workspace for which the environment should be provided.
     * @return The environment variables for the workspace.
     */
    default CompletionStage<Map<String, String>> getEnvironment(User user, String workspace) {
        return getEnvironment(user, workspace, EnvironmentType.EXTERNAL);
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
     * @param description The new description of the model.
     * @return Done.
     */
    CompletionStage<Done> updateModel(User user, String workspace, String model, String description);

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
    CompletionStage<Done> promoteModel(User user, String workspace, String model, String version,
                                       ModelVersionStage stage);

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
    CompletionStage<List<VolumeProperties>> getVolumes(User user, String workspace);

    /**
     * Create an application for app-based access to given workspace
     *
     * @param runtime       maquette runtime
     * @param user          the user who executes the action
     * @param workspaceName the workspace name
     * @param name          the application name
     * @param metaInfo      additional meta data
     * @return application object with secret
     */
    CompletionStage<Application> createApplication(MaquetteRuntime runtime, User user, String workspaceName,
                                                   String name,
                                                   String metaInfo);

    /**
     * Renew application secret
     *
     * @param runtime       maquette runtime
     * @param user          the user who executes the action
     * @param workspaceName the workspace name
     * @param name          the application name
     * @return application object with secret
     */
    CompletionStage<Done> renewApplicationSecret(MaquetteRuntime runtime, User user, String workspaceName,
                                                 String name);

    /**
     * Get information about signed-in application
     *
     * @param runtime maquette runtime
     * @param user    the user who executes the action
     * @return application object with secret
     */
    CompletionStage<Application> getOauthSelfApplication(MaquetteRuntime runtime, User user);

    /**
     * Remove an application from given workspace access
     *
     * @param runtime         maquette runtime
     * @param user            the user who executes the action
     * @param workspaceName   the workspace name
     * @param applicationName the application name
     * @return Done.
     */
    CompletionStage<Done> removeApplication(MaquetteRuntime runtime, User user, String workspaceName,
                                            String applicationName);

    /**
     * Find all applications with access to given workspace
     *
     * @param runtime       maquette runtime
     * @param user          the user who executes the action
     * @param workspaceName the workspace name
     * @return List of all applications in the workspace.
     */
    CompletionStage<List<Application>> findApplicationsInWorkspace(MaquetteRuntime runtime, User user,
                                                                   String workspaceName);
}
