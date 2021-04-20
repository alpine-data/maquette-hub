package maquette.core.entities.infrastructure.ports;

import akka.Done;
import maquette.core.entities.infrastructure.Container;
import maquette.core.entities.infrastructure.Deployment;
import maquette.core.entities.infrastructure.exceptions.VolumeNotFoundException;
import maquette.core.entities.infrastructure.model.ContainerConfig;
import maquette.core.entities.infrastructure.model.DeploymentConfig;
import maquette.core.entities.infrastructure.model.DataVolume;
import maquette.core.values.UID;
import maquette.core.values.data.binary.CompressedBinaryObject;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface InfrastructureProvider {

    CompletionStage<Deployment> runDeployment(DeploymentConfig config);

    CompletionStage<Container> runContainer(ContainerConfig config);

    /*
     * Volumes
     */

    /**
     * Creates a new empty volume.
     * @param volume The volume's metadata.
     * @return done
     */
    CompletionStage<Done> createVolume(DataVolume volume);

    /**
     * Creates a new volume with initial data. The initial data should be a zip-file
     * containing the files.
     *
     * @param volume The volume's metadata.
     * @param initialData The initial data (ZIP file).
     * @return done
     */
    CompletionStage<Done> createVolume(DataVolume volume, CompressedBinaryObject initialData);

    CompletionStage<List<DataVolume>> getVolumes();

    CompletionStage<Optional<DataVolume>> findVolumeById(UID volume);

    default CompletionStage<DataVolume> getVolumeById(UID volume) {
        return findVolumeById(volume).thenApply(opt -> opt.orElseThrow(() -> VolumeNotFoundException.apply(volume)));
    }

    CompletionStage<Done> removeVolume(UID volume);

}
