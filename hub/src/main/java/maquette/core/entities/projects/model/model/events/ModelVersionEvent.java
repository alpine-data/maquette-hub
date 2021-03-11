package maquette.core.entities.projects.model.model.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import maquette.core.values.ActionMetadata;

@JsonTypeInfo(
   use = JsonTypeInfo.Id.NAME,
   property = "event")
@JsonSubTypes(
   {
      @JsonSubTypes.Type(value = Approved.class, name = "approved"),
      @JsonSubTypes.Type(value = QuestionnaireFilled.class, name = "questionnaire-filled"),
      @JsonSubTypes.Type(value = Registered.class, name = "registered"),
      @JsonSubTypes.Type(value = Rejected.class, name = "rejected"),
      @JsonSubTypes.Type(value = ReviewRequested.class, name = "review-requested")
   })
public interface ModelVersionEvent {

   ActionMetadata getCreated();

}
