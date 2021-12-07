package maquette.development.values.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "apply")
public final class ModelPermissions {

   boolean owner;

   boolean reviewer;

   boolean member;

   @JsonProperty("canApproveModel")
   public boolean canApproveModel() {
      return reviewer;
   }

   @JsonProperty("canFillQuestionnaire")
   public boolean canFillQuestionnaire() {
      return owner || member;
   }

   @JsonProperty("canRequestReview")
   public boolean canRequestReview() {
      return owner || member;
   }

   @JsonProperty("canPromote")
   public boolean canPromote() {
      return owner || member;
   }

}
