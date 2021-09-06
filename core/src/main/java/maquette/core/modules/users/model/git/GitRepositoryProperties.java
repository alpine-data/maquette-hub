package maquette.core.modules.users.model.git;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class GitRepositoryProperties {

   String name;

   String gitTransportUrl;

   String httpTransportUrl;

}
