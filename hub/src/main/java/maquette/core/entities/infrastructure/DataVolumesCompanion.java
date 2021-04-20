package maquette.core.entities.infrastructure;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.entities.infrastructure.model.DataVolume;
import maquette.core.entities.infrastructure.ports.InfrastructureProvider;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.data.binary.CompressedBinaryObject;
import maquette.core.values.user.User;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DataVolumesCompanion {

   private final InfrastructureProvider provider;

   public CompletionStage<DataVolume> create(User executor, String name) {
      var volume = DataVolume.apply(UID.apply(), name, ActionMetadata.apply(executor));
      return provider.createVolume(volume).thenApply(d -> volume);
   }

   public CompletionStage<DataVolume> create(User executor, String name, CompressedBinaryObject initialData) {
      var volume = DataVolume.apply(UID.apply(), name, ActionMetadata.apply(executor));
      return provider.createVolume(volume, initialData).thenApply(d -> volume);
   }

   public CompletionStage<List<DataVolume>> get() {
      return provider.getVolumes();
   }

   public CompletionStage<Optional<DataVolume>> findById(UID volume) {
      return provider.findVolumeById(volume);
   }

   public CompletionStage<DataVolume> getById(UID volume) {
      return provider.getVolumeById(volume);
   }

   public CompletionStage<Done> remove(UID volume) {
      return provider.removeVolume(volume);
   }

}
