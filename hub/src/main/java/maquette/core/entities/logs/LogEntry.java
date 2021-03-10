package maquette.core.entities.logs;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.entities.projects.model.apps.Application;
import maquette.core.entities.users.model.UserProfile;
import maquette.core.values.UID;
import maquette.core.values.user.EnvironmentContext;

import java.time.Instant;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class LogEntry {

   Instant logged;

   UserProfile user;

   Application application;

   EnvironmentContext environment;

   ProjectProperties project;

   UID resource;

   Action action;

}
