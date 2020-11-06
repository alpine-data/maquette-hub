package maquette.core.services;

import akka.Done;
import maquette.core.entities.projects.model.ProjectDetails;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.values.user.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public interface ProjectServices {

    CompletionStage<Integer> create(User user, String name, String title, String summary);

    CompletionStage<Map<String, String>> environment(User user, String name);

    CompletionStage<List<ProjectProperties>> list(User user);

    CompletionStage<ProjectDetails> get(User user, String name);

    CompletionStage<Done> remove(User user, String name);

}
