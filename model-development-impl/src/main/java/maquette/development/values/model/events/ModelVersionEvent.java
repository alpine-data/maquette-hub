package maquette.development.values.model.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import maquette.core.values.ActionMetadata;

/**
 * This interface bundles events which can be logged for a model version.
 *
 * Available events
 * ----------------
 * {@link Approved} - The model has been approved by a reviewer.
 *
 * {@link AutomaticallyPromoted} - The model has been automatically promoted to a new stage. E.g., because it was deployed
 * within a service into development or production environment.
 *
 * {@link Registered} - The model has been registered in MLflow. This event is created automatically upon first load from Mlflow.
 *
 * {@link Rejected} - The model has been reviewed, but rejected.
 *
 * {@link ReviewRequested} - A model developer has requested to review the model.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "event")
@JsonSubTypes(
    {
        @JsonSubTypes.Type(value = Approved.class, name = "approved"),
        @JsonSubTypes.Type(value = AutomaticallyPromoted.class, name = "automatically-promoted"),
        @JsonSubTypes.Type(value = Registered.class, name = "registered"),
        @JsonSubTypes.Type(value = Rejected.class, name = "rejected"),
        @JsonSubTypes.Type(value = ReviewRequested.class, name = "review-requested")
    })
public interface ModelVersionEvent {

    ActionMetadata getCreated();

}
