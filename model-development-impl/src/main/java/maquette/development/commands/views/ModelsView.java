package maquette.development.commands.views;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.server.commands.CommandResult;
import maquette.core.values.ActionMetadata;
import maquette.development.values.model.ModelPermissions;
import maquette.development.values.model.ModelProperties;
import maquette.development.values.model.ModelVersion;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Value
@AllArgsConstructor(staticName = "apply")
public class ModelsView implements CommandResult {

    Map<String, ModelProperties> models;

    ModelPermissions permissions;

    List<ModelSummary> summary;

    Map<String, Map<String, ModelVersion>> versions;

    public static ModelsView apply(List<ModelProperties> models, ModelPermissions permissions) {
        Map<String, ModelProperties> modelsMapped = Maps.newHashMap();
        Map<String, Map<String, ModelVersion>> versions = Maps.newHashMap();

        var summary = models
            .stream()
            .map(m -> {
                var latestVersion = m
                    .getVersions()
                    .stream()
                    .max(Comparator.comparing(v -> v
                        .getRegistered()
                        .getAt()))
                    .map(ModelVersion::getVersion);

                Map<String, ModelVersion> modelVersions = Maps.newHashMap();
                m
                    .getVersions()
                    .forEach(v -> {
                        modelVersions.put(v.getVersion(), v);
                    });

                modelsMapped.put(m.getName(), m);
                versions.put(m.getName(), modelVersions);

                return ModelSummary.apply(
                    m.getTitle(), m.getName(), m.getFlavours(), m.getDescription(), m
                        .getVersions()
                        .size(),
                    latestVersion.orElse("-"), m.getCreated(), m.getUpdated());
            })
            .collect(Collectors.toList());

        return apply(modelsMapped, permissions, summary, versions);
    }

    @Value
    @AllArgsConstructor(staticName = "apply")
    public static class ModelSummary {

        String title;

        String name;

        Set<String> flavours;

        String description;

        int versions;

        String latestVersion;

        ActionMetadata created;

        ActionMetadata modified;

    }

}
