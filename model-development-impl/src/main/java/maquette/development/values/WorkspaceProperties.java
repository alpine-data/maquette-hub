package maquette.development.values;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.development.values.stacks.MlflowStackConfiguration;
import maquette.development.values.stacks.VolumeProperties;

import java.util.List;
import java.util.Optional;

@With
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkspaceProperties {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String TITLE = "title";
    private static final String SUMMARY = "summary";
    private static final String CREATED = "created";
    private static final String MODIFIED = "modified";
    private static final String ML_FLOW_CONFIGURATION = "mlFlowConfiguration";
    private static final String VOLUMES = "volumes";

    @JsonProperty(ID)
    UID id;

    @JsonProperty(NAME)
    String name;

    @JsonProperty(TITLE)
    String title;

    @JsonProperty(SUMMARY)
    String summary;

    @JsonProperty(CREATED)
    ActionMetadata created;

    @JsonProperty(MODIFIED)
    ActionMetadata modified;

    @JsonProperty(ML_FLOW_CONFIGURATION)
    MlflowStackConfiguration mlFlowConfiguration;

    @JsonProperty(VOLUMES)
    List<VolumeProperties> volumes;

    @JsonCreator
    public static WorkspaceProperties apply(
        @JsonProperty(ID) UID id,
        @JsonProperty(NAME) String name,
        @JsonProperty(TITLE) String title,
        @JsonProperty(SUMMARY) String summary,
        @JsonProperty(CREATED) ActionMetadata created,
        @JsonProperty(MODIFIED) ActionMetadata modified,
        @JsonProperty(ML_FLOW_CONFIGURATION) MlflowStackConfiguration mlFlowConfiguration,
        @JsonProperty(VOLUMES) List<VolumeProperties> volumes) {
        return new WorkspaceProperties(id, name, title, summary, created, modified, mlFlowConfiguration, volumes);
    }

    public static WorkspaceProperties apply(
        UID id, String name, String title, String summary, ActionMetadata created, ActionMetadata modified) {

        return apply(id, name, title, summary, created, modified, null, Lists.newArrayList());
    }

    public Optional<MlflowStackConfiguration> getMlFlowConfiguration() {
        return Optional.ofNullable(mlFlowConfiguration);
    }

}
