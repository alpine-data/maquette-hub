package maquette.core.values.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class DataAssetPermissions {

   boolean owner;
   boolean steward;
   boolean consumer;
   boolean producer;
   boolean member;
   boolean subscriber;

   @JsonProperty("canChangeSettings")
   public boolean canChangeSettings() {
      return owner || steward;
   }

   @JsonProperty("canManageAccessRequests")
   public boolean canManageAccessRequests() {
      return owner || steward;
   }

   @JsonProperty("canConsume")
   public boolean canConsume() {
      return owner || steward || consumer || member || subscriber;
   }

   @JsonProperty("canManageState")
   public boolean canManageState() {
      return owner;
   }

   @JsonProperty("canProduce")
   public boolean canProduce() {
      return owner || producer || steward || member;
   }

   @JsonProperty("canReviewLogs")
   public boolean canReviewLogs() {
      return owner || steward;
   }
}
