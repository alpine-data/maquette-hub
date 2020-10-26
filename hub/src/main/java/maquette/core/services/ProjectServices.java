package maquette.core.services;

import akka.Done;
import maquette.core.entities.projects.model.ProjectSummary;
import maquette.core.values.user.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public interface ProjectServices {

    CompletionStage<Integer> create(User user, String name);

    CompletionStage<Map<String, String>> environment(User user, String name);

    CompletionStage<List<ProjectSummary>> list(User user);

    CompletionStage<Done> remove(User user, String name);

}
