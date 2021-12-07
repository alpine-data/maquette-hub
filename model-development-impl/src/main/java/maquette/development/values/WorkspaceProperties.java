package maquette.development.values;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;

import java.util.Optional;

@With
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkspaceProperties {

   private static final String ID = "id";
   private static final String NAME = "name";
   private static final String TITLE = "title";
   private static final String SUMMARY = "summary";
   private static final String CREATED = "created";
   private static final String MODIFIED = "modified";
   private static final String ML_FLOW_CONFIGURATION = "mlFlowConfiguration";

   @JsonProperty(ID)
   UID id;

   @JsonProperty(NAME)
   String name;

   @JsonProperty(TITLE)
   String title;

   @JsonProperty(SUMMARY)
   String summary;

   @JsonProperty(CREATED)
   ActionMetadata created;

   @JsonProperty(MODIFIED)
   ActionMetadata modified;

   @JsonProperty(ML_FLOW_CONFIGURATION)
   MlflowConfiguration mlFlowConfiguration;

   @JsonCreator
   public static WorkspaceProperties apply(
       @JsonProperty(ID) UID id, @JsonProperty(NAME) String name, @JsonProperty(TITLE) String title,
       @JsonProperty(SUMMARY) String summary, @JsonProperty(CREATED) ActionMetadata created,
       @JsonProperty(MODIFIED) ActionMetadata modified, @JsonProperty(ML_FLOW_CONFIGURATION) MlflowConfiguration mlFlowConfiguration) {

      return new WorkspaceProperties(id, name, title, summary, created, modified, mlFlowConfiguration);
   }

   public static WorkspaceProperties apply(
      UID id, String name, String title, String summary, ActionMetadata created, ActionMetadata modified) {

      return apply(id, name, title, summary, created, modified, null);
   }

   public Optional<MlflowConfiguration> getMlFlowConfiguration() {
      return Optional.ofNullable(mlFlowConfiguration);
   }

}
