package maquette.datashop.values.access_requests;

import maquette.core.values.ActionMetadata;
import maquette.datashop.values.access_requests.events.DataAccessRequestEvent;

public interface DataAccessRequestUserTriggeredEvent extends DataAccessRequestEvent {

    ActionMetadata getCreated();

}
