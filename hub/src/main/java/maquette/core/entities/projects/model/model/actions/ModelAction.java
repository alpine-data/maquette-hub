package maquette.core.entities.projects.model.model.actions;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
   use = JsonTypeInfo.Id.NAME,
   property = "type")
@JsonSubTypes(
   {
      // Collections
      @JsonSubTypes.Type(value = ApproveModel.class, name = "approve"),
      @JsonSubTypes.Type(value = ArchiveModel.class, name = "archive"),
      @JsonSubTypes.Type(value = FillQuestionnaire.class, name = "fill-questionnaire"),
      @JsonSubTypes.Type(value = PromoteModel.class, name = "promote"),
      @JsonSubTypes.Type(value = RequestReview.class, name = "request-review"),
      @JsonSubTypes.Type(value = RestoreModel.class, name = "restore"),
      @JsonSubTypes.Type(value = ReviewQuestionnaire.class, name = "review-questionnaire"),
      @JsonSubTypes.Type(value = ViewSource.class, name = "view-source"),
   })
public interface ModelAction {
}
