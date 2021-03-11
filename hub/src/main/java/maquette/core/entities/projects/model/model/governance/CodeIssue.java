package maquette.core.entities.projects.model.model.governance;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class CodeIssue {

   String location;

   IssueType type;

   String message;

}
