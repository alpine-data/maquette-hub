package maquette.core.server.views;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.core.entities.infrastructure.model.DataVolume;
import maquette.core.entities.projects.model.Project;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.entities.projects.model.sandboxes.stacks.StackProperties;
import maquette.core.server.CommandResult;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class CreateSandboxView implements CommandResult {

   Project project;

   List<StackProperties> stacks;

   List<String> gitRepositories;

   List<DataVolume> volumes;

   @JsonProperty("randomName")
   public String getRandomName() {
      return Operators.random_name();
   }

}
