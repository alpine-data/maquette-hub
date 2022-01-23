package maquette.development.ports.infrastructure.docker;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.values.UID;
import maquette.core.values.binary.CompressedBinaryObject;
import maquette.development.ports.infrastructure.docker.model.DataVolume;
import org.apache.commons.io.FileUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class DockerVolumeCompanion {

   private final Path data;

   private final ObjectMapper om;

   public Path getVolumesDirectory() {
      var file = data.resolve("infrastructure").resolve("volumes");
      Operators.suppressExceptions(() -> Files.createDirectories(file));
      return file;
   }

   public Path getVolumeDirectory(UID volume) {
      var file = getVolumesDirectory().resolve(volume.getValue());
      Operators.suppressExceptions(() -> Files.createDirectories(file));
      return file;
   }

   public Path getVolumeFile(UID volume) {
      return getVolumesDirectory().resolve(String.format("%s.volume.json", volume.getValue()));
   }

   public void createVolume(DataVolume volume) {
      var file = getVolumeFile(volume.getId());
      getVolumeDirectory(volume.getId());
      Operators.suppressExceptions(() -> om.writeValue(file.toFile(), volume));
   }

   public void createVolume(DataVolume volume, CompressedBinaryObject initialData) {
      createVolume(volume);
      initialData.toFile(getVolumeDirectory(volume.getId()));
   }

   public CompletionStage<List<DataVolume>> getVolumes() {
      var result = Operators
         .suppressExceptions(() -> Files.walk(getVolumesDirectory()))
         .filter(p -> p.getFileName().toString().endsWith("volume.json"))
         .map(file -> Operators.ignoreExceptionsWithDefault(
            () -> om.readValue(file.toFile(), DataVolume.class),
            null
         ))
         .filter(Objects::nonNull)
         .collect(Collectors.toList());

      return CompletableFuture.completedFuture(result);
   }

   public CompletionStage<Optional<DataVolume>> findVolumeById(UID volume) {
      return getVolumes().thenApply(volumes -> volumes
         .stream()
         .filter(v -> v.getId().equals(volume))
         .findFirst());
   }

   public CompletionStage<Done> removeVolume(UID volume) {
      Operators.suppressExceptions(() -> {
         FileUtils.deleteDirectory(getVolumeDirectory(volume).toFile());
         Files.delete(getVolumeFile(volume));
      });

      return CompletableFuture.completedFuture(Done.getInstance());
   }

}
