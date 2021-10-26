package maquette.datashop.values.access_requests;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import maquette.core.values.ActionMetadata;

import java.time.Instant;

public interface DataAccessRequestUserTriggeredEvent extends DataAccessRequestEvent {

    ActionMetadata getCreated();

}
