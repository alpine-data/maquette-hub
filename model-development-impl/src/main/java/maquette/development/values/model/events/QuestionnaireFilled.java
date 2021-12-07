package maquette.development.values.model.events;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.values.ActionMetadata;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class QuestionnaireFilled implements ModelVersionEvent {

   ActionMetadata created;

}
