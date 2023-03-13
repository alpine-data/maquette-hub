package maquette.development.values.model.actions;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Interface to model available actions for model versions.
 *
 * A model version can derive potential next actions from its state. Each action may contain
 * additional meta information.
 *
 * Overview of available actions
 * -----------------------------
 *
 * {@link ApproveModel} can be done my reviews of the model to indicate that the model code and other required reviews
 * have been done successfully and the model is approved to be promoted into production state.
 *
 * {@link ArchiveModel} can be executed to archive the model (this is also a state within MLflow) to indicate that a model
 * should not be used anymore.
 *
 * {@link PromoteModel} can be executed by model developers to move a model to the next development stage.
 *
 * {@link RequestReview} can be triggered by developer to notify model reviewers to review the model.
 *
 * @
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type")
@JsonSubTypes(
    {
        @JsonSubTypes.Type(value = ApproveModel.class, name = "approve"),
        @JsonSubTypes.Type(value = ArchiveModel.class, name = "archive"),
        @JsonSubTypes.Type(value = PromoteModel.class, name = "promote"),
        @JsonSubTypes.Type(value = RequestReview.class, name = "request-review"),
    })
public interface ModelAction {
}
