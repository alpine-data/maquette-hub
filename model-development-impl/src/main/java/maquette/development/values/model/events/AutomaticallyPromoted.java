package maquette.development.values.model.events;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.values.ActionMetadata;
import maquette.core.values.user.AnonymousUser;
import maquette.development.values.model.ModelVersionStage;
import maquette.development.values.model.ModelVersionState;

import java.time.Instant;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class AutomaticallyPromoted implements ModelVersionEvent {

    Instant moment;

    ModelVersionStage stage;

    @Override
    public ActionMetadata getCreated() {
        return ActionMetadata.apply(AnonymousUser.apply(), moment);
    }
}
