package maquette.development.values.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.ActionMetadata;
import maquette.development.values.exceptions.ModelVersionNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class ModelProperties {

    String title;

    String name;

    String url;

    Set<String> flavours;

    String description;

    List<String> warnings;

    List<ModelVersion> versions;

    ActionMetadata created;

    ActionMetadata updated;

    public ModelVersion getVersion(String version) {
        return findVersion(version).orElseThrow(() -> ModelVersionNotFoundException.apply(name, version));
    }

    public Optional<ModelVersion> findVersion(String version) {
        return versions
            .stream()
            .filter(v -> v
                .getVersion()
                .equals(version))
            .findAny();
    }

    public ModelProperties withVersion(ModelVersion version) {
        var filtered = this
            .versions
            .stream()
            .filter(v -> !v
                .getVersion()
                .equals(version.getVersion()));

        var versions = Stream
            .concat(filtered, Stream.of(version))
            .collect(Collectors.toList());

        return withVersions(versions);
    }

}
