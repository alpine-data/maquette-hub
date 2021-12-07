package maquette.development.values.model.events;

import maquette.development.values.model.ModelVersionState;

public interface StateChangedEvent {

   ModelVersionState getState();

}
