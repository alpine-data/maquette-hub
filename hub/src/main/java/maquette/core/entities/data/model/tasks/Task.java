package maquette.core.entities.data.model.tasks;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import maquette.core.entities.data.model.DataAssetProperties;
import maquette.core.values.data.DataAssetPermissions;

@JsonTypeInfo(
   use = JsonTypeInfo.Id.NAME,
   property = "task")
@JsonSubTypes(
   {
      @JsonSubTypes.Type(value = AnswerAccessRequests.class, name = "answer-access-requests"),
      @JsonSubTypes.Type(value = ReviewAsset.class, name = "review")
   })
public interface Task {

   DataAssetProperties getAsset();

   boolean canExecuteTask(DataAssetPermissions permissions);

}
