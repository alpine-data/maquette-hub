package maquette.core.entities.projects.ports;

import akka.Done;
import maquette.core.entities.projects.model.apps.Application;
import maquette.core.values.UID;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ApplicationsRepository {

   CompletionStage<Done> insertOrUpdateApplication(UID project, Application app);

   CompletionStage<Optional<Application>> findApplicationByName(UID project, String name);

   CompletionStage<Optional<Application>> findApplicationById(UID project, UID id);

   CompletionStage<List<Application>> listApplications(UID project);

   CompletionStage<Done> removeApplication(UID project, UID id);

}
