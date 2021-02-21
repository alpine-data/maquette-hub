package maquette.core.services.projects;

import akka.Done;
import com.fasterxml.jackson.databind.JsonNode;
import maquette.core.entities.projects.model.model.Model;
import maquette.core.entities.projects.model.model.ModelProperties;
import maquette.core.entities.projects.model.Project;
import maquette.core.entities.projects.model.ProjectMemberRole;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.entities.projects.model.model.ModelMemberRole;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.user.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ProjectServices {

    CompletionStage<Done> create(User user, String name, String title, String summary);

    CompletionStage<Map<String, String>> environment(User user, String name, EnvironmentType environmentType);

    default CompletionStage<Map<String, String>> environment(User user, String name) {
        return environment(user, name, EnvironmentType.EXTERNAL);
    }

    CompletionStage<List<ProjectProperties>> list(User user);

    CompletionStage<Project> get(User user, String name);

    CompletionStage<Done> remove(User user, String name);

    CompletionStage<Done> update(User user, String name, String updatedName, String title, String summary);

    /*
     * Models
     */
    CompletionStage<List<ModelProperties>> getModels(User user, String name);

    CompletionStage<Model> getModel(User user, String project, String model);

    CompletionStage<Done> updateModel(User user, String project, String model, String title, String description);

    CompletionStage<Done> answerQuestionnaire(User user, String project, String model, String version, JsonNode responses);

    CompletionStage<Done> approveModel(User user, String project, String model, String version);

    CompletionStage<Optional<JsonNode>> getLatestQuestionnaireAnswers(User user, String project, String model);

    /*
     * Manage model roles
     */
    CompletionStage<Done> grantModelRole(User user, String project, String model, UserAuthorization authorization, ModelMemberRole role);

    CompletionStage<Done> revokeModelRole(User user, String name, String model, UserAuthorization authorization);


    /*
     * Manage members
     */
    CompletionStage<Done> grant(User user, String name, Authorization authorization, ProjectMemberRole role);

    CompletionStage<Done> revoke(User user, String name, Authorization authorization);

}
