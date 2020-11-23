package maquette.core.values.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.values.ActionMetadata;

@Value
@AllArgsConstructor(staticName = "apply")
public class LinkedDataAsset implements DataAssetProperties {

   ProjectProperties project;

   DataAssetProperties linked;

   @Override
   @JsonIgnore
   public String getId() {
      return linked.getId();
   }

   @Override
   @JsonIgnore
   public String getTitle() {
      return linked.getTitle();
   }

   @Override
   @JsonIgnore
   public String getName() {
      return linked.getName();
   }

   @Override
   @JsonIgnore
   public String getSummary() {
      return linked.getSummary();
   }

   @Override
   @JsonIgnore
   public ActionMetadata getCreated() {
      return linked.getCreated();
   }

   @Override
   @JsonIgnore
   public ActionMetadata getUpdated() {
      return linked.getUpdated();
   }

}
