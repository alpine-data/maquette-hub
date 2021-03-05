package maquette.core.values.data;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class DataAssetPermissions {

   public static DataAssetPermissions apply(
      boolean isOwner, boolean isSteward, boolean isConsumer, boolean isProducer, boolean isMember, boolean isSubscriber) {

      var canProduce = isOwner || isSteward || isProducer || isMember;
      var canConsume = isOwner || isSteward || isConsumer || isMember || isSubscriber;
      var canChangeSettings = isOwner || isSteward;
      var canReviewLogs = isOwner || isSteward;

      return apply(canProduce, canConsume, canChangeSettings, canReviewLogs);
   }

   boolean canProduce;

   boolean canConsume;

   boolean canChangeSettings;

   boolean canReviewLogs;

   public boolean canChangeSettings() {
      return canChangeSettings;
   }

   public boolean canConsume() {
      return canConsume;
   }

   public boolean canProduce() {
      return canProduce;
   }

   public boolean canReviewLogs() {
      return canReviewLogs;
   }
}
