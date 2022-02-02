package maquette.development.values.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.development.values.model.governance.CheckExemption;
import maquette.development.values.model.governance.CheckWarning;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class Model {

    ModelProperties properties;

    List<GrantedAuthorization<ModelMemberRole>> members;

    ModelPermissions permissions;

    public static Model fromProperties(ModelProperties properties,
                                       List<GrantedAuthorization<ModelMemberRole>> members,
                                       ModelPermissions permissions) {
        return apply(properties,
            members,
            permissions);
    }

    public Optional<ModelVersion> findVersion(String version) {
        return properties.getVersions()
            .stream()
            .filter(v -> v.getVersion().equals(version))
            .findAny();
    }

    @JsonProperty("exceptions")
    public long getExceptions() {
        return properties.getVersions()
            .stream()
            .max(Comparator.comparing(m -> m.getRegistered().getAt()))
            .map(version -> {
                var count = version.getCodeQualityChecks().stream().filter(r -> r instanceof CheckExemption).count();
                count += version.getDataDependencyChecks().stream().filter(r -> r instanceof CheckExemption).count();

                return count;
            })
            .orElse(0L);
    }

    @JsonProperty("warnings")
    public long getWarnings() {
        return properties.getVersions()
            .stream()
            .max(Comparator.comparing(m -> m.getRegistered().getAt()))
            .map(version -> {
                var count = version.getCodeQualityChecks().stream().filter(r -> r instanceof CheckWarning).count();
                count += version.getDataDependencyChecks().stream().filter(r -> r instanceof CheckWarning).count();

                return count;
            })
            .orElse(0L);
    }
}
