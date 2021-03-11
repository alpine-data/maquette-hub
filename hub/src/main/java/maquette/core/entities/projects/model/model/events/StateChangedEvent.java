package maquette.core.entities.projects.model.model.events;

import maquette.core.entities.projects.model.model.ModelVersionState;

public interface StateChangedEvent {

   ModelVersionState getState();

}
