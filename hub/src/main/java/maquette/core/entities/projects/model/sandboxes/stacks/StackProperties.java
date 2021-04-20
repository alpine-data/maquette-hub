package maquette.core.entities.projects.model.sandboxes.stacks;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.common.forms.Form;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class StackProperties {

   String title;

   String name;

   String summary;

   String icon;

   List<String> tags;

   Form configurationForm;

}
