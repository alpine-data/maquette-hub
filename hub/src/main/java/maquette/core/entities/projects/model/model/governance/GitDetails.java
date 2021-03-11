package maquette.core.entities.projects.model.model.governance;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

import java.util.Optional;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class GitDetails {

   String commit;

   String transferUrl;

   boolean master;

   public Optional<String> getCommit() {
      return Optional.of(commit);
   }

   public Optional<String> getTransferUrl() {
      return Optional.of(transferUrl);
   }

}
