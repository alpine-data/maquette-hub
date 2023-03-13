package maquette.development.values;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.development.values.mlproject.MachineLearningProject;
import maquette.development.values.stacks.MlflowStackConfiguration;
import maquette.development.values.stacks.VolumeProperties;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private static final String PROJECTS = "projects";

    /**
     * The unique id of the workspace. Usually this ID is only used internally and not directly shown
     * to the users.
     */
    @JsonProperty(ID)
    UID id;

    /**
     * The technical name of the workspace. This name can be used to fetch information of the workspace.
     */
    @JsonProperty(NAME)
    String name;

    /**
     * A human-readable name of the workspace.
     */
    @JsonProperty(TITLE)
    String title;

    /**
     * The summary of the workspace. A short description describing its purpose.
     */
    @JsonProperty(SUMMARY)
    String summary;

    /**
     * Creation metadata.
     */
    @JsonProperty(CREATED)
    ActionMetadata created;

    /**
     * Last updated metadata.
     */
    @JsonProperty(MODIFIED)
    ActionMetadata modified;

    /**
     * The MLflow stack configuration of the workspace. Each workspace gets its own instance of MLflow.
     * This instance is created during creation of the Workspace.
     * As soon as the MLflow stack is initialize, the workspace will be updated with its information.
     */
    @JsonProperty(ML_FLOW_CONFIGURATION)
    MlflowStackConfiguration mlFlowConfiguration;

    /**
     * Information about available volumes within this workspace. Volumes can be created with and for sandboxes.
     * One volume may be related to multiple sandboxes of a workspaces. But volumes can only be access by the user
     * owning a volume.
     */
    @JsonProperty(VOLUMES)
    List<VolumeProperties> volumes;

    /**
     * A workspace may reference Machine Learning projects which use this workspace.
     */
    @JsonProperty(PROJECTS)
    List<MachineLearningProject> projects;

    @JsonCreator
    public static WorkspaceProperties apply(
        @JsonProperty(ID) UID id,
        @JsonProperty(NAME) String name,
        @JsonProperty(TITLE) String title,
        @JsonProperty(SUMMARY) String summary,
        @JsonProperty(CREATED) ActionMetadata created,
        @JsonProperty(MODIFIED) ActionMetadata modified,
        @JsonProperty(ML_FLOW_CONFIGURATION) MlflowStackConfiguration mlFlowConfiguration,
        @JsonProperty(VOLUMES) List<VolumeProperties> volumes,
        @JsonProperty(PROJECTS) List<MachineLearningProject> projects) {

        if (Objects.isNull(volumes)) {
            volumes = List.of();
        }

        if (Objects.isNull(projects)) {
            projects = List.of();
        }

        return new WorkspaceProperties(
            id, name, title, summary, created, modified, mlFlowConfiguration, List.copyOf(volumes), List.copyOf(projects)
        );
    }

    public static WorkspaceProperties apply(
        UID id, String name, String title, String summary, ActionMetadata created, ActionMetadata modified) {

        return apply(id, name, title, summary, created, modified, null, List.of(), List.of());
    }

    public Optional<MlflowStackConfiguration> getMlFlowConfiguration() {
        return Optional.ofNullable(mlFlowConfiguration);
    }

    public WorkspaceProperties withProject(MachineLearningProject mlProject) {
        var newProjects = Stream
            .concat(
                /*
                 * We filter based on all attributes, because the combination must be unique.
                 */
                this
                    .projects
                    .stream()
                    .filter(p -> !p.getName().equals(mlProject.getName()))
                    .filter(p -> !p.getUrl().equals(mlProject.getUrl()))
                    .filter(p -> !p.getGitUrl().equals(mlProject.getGitUrl()))
                    .filter(p -> !p.getCatalogUrl().equals(mlProject.getCatalogUrl())),
                Stream.of(mlProject)
            )
            .collect(Collectors.toList());

        return withProjects(newProjects);
    }

    public WorkspaceProperties withVolume(VolumeProperties volume) {
        var newVolumes = Stream
            .concat(
                /*
                 * We filter based on Unique IDs (ID + Name).
                 */
                this
                    .volumes
                    .stream()
                    .filter(v -> !v.getId().equals(volume.getId()))
                    .filter(v -> !v.getName().equals(volume.getName())),
                Stream.of(volume)
            )
            .collect(Collectors.toList());

        return withVolumes(newVolumes);
    }

}
