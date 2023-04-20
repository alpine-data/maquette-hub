package maquette.development.values.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.ActionMetadata;
import maquette.development.entities.mlflow.explainer.ExplainerArtifact;
import maquette.development.values.model.actions.*;
import maquette.development.values.model.events.Approved;
import maquette.development.values.model.events.ModelVersionEvent;
import maquette.development.values.model.events.Registered;
import maquette.development.values.model.events.StateChangedEvent;
import maquette.development.values.model.governance.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class represents a model version.
 */
@With
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelVersion {

    private final static String VERSION = "version";
    private final static String REGISTERED = "registered";
    private final static String UPDATED = "updated";
    private static final String FLAVOURS = "flavours";
    private final static String STAGE = "stage";
    private final static String CODE_QUALITY = "codeQuality";
    private final static String GIT_DETAILS = "gitDetails";
    private final static String DATA_DEPENDENCIES = "dataDependencies";
    private final static String EXPLAINERS = "explainers";
    private final static String EVENTS = "events";

    private final static String ACTIONS = "actions";
    private final static String CODE_QUALITY_CHECKS = "codeQualityChecks";
    private final static String CODE_QUALITY_SUMMARY = "codeQualitySummary";
    private final static String DATA_DEPENDENCY_CHECKS = "dataDependencyChecks";
    private final static String DATA_DEPENDENCY_SUMMARY = "dataDependencySummary";
    private final static String STATE = "state";

    private final static String RUN_ID= "runId";

    /**
     * The version identifier as listed in MLflow.
     */
    @JsonProperty(VERSION)
    String version;

    /**
     * The moment in which the version was registered in MLflow.
     */
    @JsonProperty(REGISTERED)
    ActionMetadata registered;

    /**
     * The moment in which properties of the model have been updated.
     */
    @JsonProperty(UPDATED)
    ActionMetadata updated;

    /**
     * The flavours of the model, as identified my MLflow.
     */
    @JsonProperty(FLAVOURS)
    Set<String> flavours;

    /**
     * The stage in which the model is currently (should be in sync with MLflow).
     */
    @JsonProperty(STAGE)
    ModelVersionStage stage;

    /**
     * Extracted information about code quality of the model.
     */
    @JsonProperty(CODE_QUALITY)
    CodeQuality codeQuality;

    /**
     * Information about the Git repository in which model code is managed.
     */
    @JsonProperty(GIT_DETAILS)
    GitDetails gitDetails;

    /**
     * Information about tracked data dependencies of the model.
     * This information is collected during training of the model within
     * a Sandbox.
     */
    @JsonProperty(DATA_DEPENDENCIES)
    DataDependencies dataDependencies;

    /**
     * Information about a model explainer which might have been logged with MLflow.
     */
    @JsonProperty(EXPLAINERS)
    List<ExplainerArtifact> explainers;

    /**
     * A list of (user-)actions which have been taken on the model.
     */
    @JsonProperty(EVENTS)
    List<ModelVersionEvent> events;

    /**
     * Information about a model explainer which might have been logged with MLflow.
     */
    @JsonProperty(RUN_ID)
    String runId;


    /**
     * Creates a new instance (from JSON).
     *
     * @param version See {@link ModelVersion#version}.
     * @param registered See {@link ModelVersion#registered}.
     * @param updated See {@link ModelVersion#updated}.
     * @param stage See {@link ModelVersion#stage}.
     * @param codeQuality See {@link ModelVersion#codeQuality}.
     * @param gitDetails See {@link ModelVersion#gitDetails}.
     * @param dataDependencies See {@link ModelVersion#dataDependencies}.
     * @param events See {@link ModelVersion#events}.
     * @param explainers See {@link ModelVersion#explainers}.
     * @return A new instance.
     */
    @JsonCreator
    public static ModelVersion apply(
        @JsonProperty(VERSION) String version,
        @JsonProperty(REGISTERED) ActionMetadata registered,
        @JsonProperty(UPDATED) ActionMetadata updated,
        @JsonProperty(FLAVOURS) Set<String> flavours,
        @JsonProperty(STAGE) ModelVersionStage stage,
        @JsonProperty(CODE_QUALITY) CodeQuality codeQuality,
        @JsonProperty(GIT_DETAILS) GitDetails gitDetails,
        @JsonProperty(DATA_DEPENDENCIES) DataDependencies dataDependencies,
        @JsonProperty(EXPLAINERS) List<ExplainerArtifact> explainers,
        @JsonProperty(EVENTS) List<ModelVersionEvent> events,
        @JsonProperty(RUN_ID) String runId
    ) {
        if (Objects.isNull(explainers)) {
            explainers = List.of();
        }

        if (Objects.isNull(events)) {
            events = List.of();
        }

        return new ModelVersion(
            version, registered, updated, Set.copyOf(flavours), stage,
            codeQuality, gitDetails, dataDependencies, List.copyOf(explainers), List.copyOf(events), runId
        );
    }

    /**
     * Creates a new instance with properties retrieved from MLflow.
     *
     * @param version See {@link ModelVersion#version}.
     * @param registered See {@link ModelVersion#registered}.
     * @param flavours See {@link ModelVersion#flavours}.
     * @param stage See {@link ModelVersion#stage}.
     * @return A new instance.
     */
    public static ModelVersion apply(
        String version, ActionMetadata registered, Set<String> flavours, ModelVersionStage stage, String runId) {
        return apply(
            version, registered, registered, flavours, stage, null, null, null, List.of(), List.of(Registered.apply(registered)), runId);
    }

    /**
     * Creates a fake instance of the object. Might be used for simple tests.
     *
     * @return A new dummy instance.
     */
    public static ModelVersion fake() {
        return apply("1", ActionMetadata.apply("egon"), Set.of("python", "java"), ModelVersionStage.NONE, "");
    }

    /**
     * Returns a set of potential next actions for the model version. The actions are selected
     * based upon current state of the model.
     * <p>
     * This method does not decide whether a user who requests the model version has the authorization
     * to execute these actions. The UI will only display actions according to related authorizations,
     * the service layer will ensure that actions are only executed by authorized users.
     *
     * @return A list of potential next actions.
     */
    @JsonProperty(ACTIONS)
    public List<ModelAction> getActions() {
        var actions = Lists.<ModelAction>newArrayList();

        /*
         * Add option to request a review if not done already.
         */
        if (getApproved().isEmpty()  && !getState().equals(ModelVersionState.REVIEW_REQUESTED)) {
            actions.add(RequestReview.apply());
        }

        /*
         * Add option to approve/ review the model.
         */
        if (getState().equals(ModelVersionState.REVIEW_REQUESTED)) {
            actions.add(ApproveModel.apply());
        }

        /*
         * Add options to promote the model to one of the next stages.
         */
        if (!stage.equals(ModelVersionStage.ARCHIVED)) {
            if (stage.equals(ModelVersionStage.NONE)) {
                actions.add(PromoteModel.apply(ModelVersionStage.STAGING));
            }

            if ((stage.equals(ModelVersionStage.STAGING) || stage.equals(ModelVersionStage.NONE)) && getApproved().isPresent()) {
                actions.add(PromoteModel.apply(ModelVersionStage.PRODUCTION));
            }
        }

        /*
         * Add options to archive the model.
         */
        if (!stage.equals(ModelVersionStage.ARCHIVED)) {
            actions.add(ArchiveModel.apply());
        }

        return actions;
    }

    /**
     * Checks whether the model version has been approved. If yes, the metadata of the action are returned.
     * @return The metadata of when the model has been approved, if approved, otherwise nothing.
     */
    public Optional<ActionMetadata> getApproved() {
        return events
            .stream()
            .filter(event -> event instanceof Approved)
            .sorted(Comparator
                .<ModelVersionEvent, Instant>comparing(e -> e
                    .getCreated()
                    .getAt())
                .reversed())
            .map(ModelVersionEvent::getCreated)
            .findFirst();
    }

    /**
     * Return code quality details if available.
     *
     * @return Code quality details.
     */
    public Optional<CodeQuality> getCodeQuality() {
        return Optional.ofNullable(codeQuality);
    }

    /**
     * Based on code quality and git information we execute several checks to make an indication
     * of the overall code quality.
     *
     * @return A list of check results for each check which is executed.
     */
    @JsonProperty(CODE_QUALITY_CHECKS)
    public List<CheckResult> getCodeQualityChecks() {
        List<CheckResult> result = Lists.newArrayList();

        if (gitDetails == null || gitDetails
            .getCommit()
            .isEmpty()) {
            result.add(CheckExemption.apply("Code is not tracked with Git"));
        } else {
            result.add(CheckOk.apply(String.format("Code is tracked with Git. The commit is `%s`", gitDetails
                .getCommit()
                .get())));
        }

        if (gitDetails == null || gitDetails
            .getTransferUrl()
            .isEmpty()) {
            result.add(CheckExemption.apply("Code is not tracked with central Git repository"));
        } else {
            result.add(CheckOk.apply(String.format("Code is tracked at `%s`", gitDetails
                .getTransferUrl()
                .get())));
        }

        if (gitDetails == null || !gitDetails.isMainBranch()) {
            result.add(CheckExemption.apply("Code is not merged in main branch"));
        } else {
            result.add(CheckOk.apply("Code merged in main branch"));
        }

        if (codeQuality == null) {
            result.add(CheckExemption.apply("Code quality is not measured."));
            result.add(CheckExemption.apply("Test coverage is not measured."));
        } else {
            result.add(CheckOk.apply("Code quality is tracked."));

            var critical = codeQuality
                .getIssues()
                .stream()
                .filter(i -> i
                    .getType()
                    .equals(IssueType.CRITICAL))
                .count();

            var warnings = codeQuality
                .getIssues()
                .stream()
                .filter(i -> i
                    .getType()
                    .equals(IssueType.WARNING))
                .count();

            if (critical > 0) {
                result.add(CheckExemption.apply("%d critical code quality issue(s)"));
            } else {
                result.add(CheckOk.apply("No critical code quality issues"));
            }

            if (warnings > 0) {
                result.add(CheckWarning.apply("%d minor code quality issue(s)"));
            } else {
                result.add(CheckOk.apply("No minor code quality issues"));
            }

            if (codeQuality.getTestCoverage() < 60) {
                result.add(CheckWarning.apply(
                    String.format("Test coverage is below 60%% - Currently %d%%", codeQuality.getTestCoverage())));
            } else {
                result.add(CheckOk.apply(String.format("Test coverage of %s%%", codeQuality.getTestCoverage())));
            }
        }

        return result;
    }

    /**
     * Returns a single sentence summary of all model version checks. It's intended to be displayed in model
     * overviews on the UI.
     *
     * @return A short statement about the model versions quality.
     */
    @JsonProperty(CODE_QUALITY_SUMMARY)
    public String getCodeQualitySummary() {
        var checks = getCodeQualityChecks();
        var exceptions = checks
            .stream()
            .filter(r -> r instanceof CheckExemption)
            .count();
        var warnings = checks
            .stream()
            .filter(r -> r instanceof CheckWarning)
            .count();

        if (exceptions > 0) {
            return String.format("%d exceptions, %d warnings", exceptions, warnings);
        } else if (warnings > 0) {
            return String.format("%d warnings", warnings);
        } else {
            return "Ned Flanders says \"Everything is diddly doodly fine!\"";
        }
    }

    /**
     * Return data dependency information if present.
     *
     * @return Data dependency information or nothing.
     */
    public Optional<DataDependencies> getDataDependencies() {
        return Optional.ofNullable(dataDependencies);
    }

    /**
     * Several checks which are made based on data dependencies tracked during model training.
     *
     * @return A list of check results.
     */
    @JsonProperty(DATA_DEPENDENCY_CHECKS)
    public List<CheckResult> getDataDependencyChecks() {
        List<CheckResult> result = Lists.newArrayList();

        if (dataDependencies == null) {
            result.add(CheckWarning.apply("Data dependencies not tracked."));
        } else {
            result.add(CheckOk.apply("Data dependencies tracked"));
        }

        return result;
    }

    /**
     * Return a short summary statement about dependency checks. It's intended to be displayed on model overviews
     * on the UI.
     *
     * @return A single statement summarizing data dependency checks.
     */
    @JsonProperty(DATA_DEPENDENCY_SUMMARY)
    public String getDataDependencySummary() {
        var checks = getDataDependencyChecks();
        var exceptions = checks
            .stream()
            .filter(r -> r instanceof CheckExemption)
            .count();
        var warnings = checks
            .stream()
            .filter(r -> r instanceof CheckWarning)
            .count();

        if (exceptions > 0) {
            return String.format("%d exceptions, %d warnings", exceptions, warnings);
        } else if (warnings > 0) {
            return String.format("%d warnings", warnings);
        } else {
            return "Homer Simpson says \"We'll just sit back, relax, and enjoy the sweet taste of success. Woo hoo!\"";
        }
    }

    /**
     * Return Git details if present.
     *
     * @return Git details or nothing.
     */
    public Optional<GitDetails> getGitDetails() {
        return Optional.ofNullable(gitDetails);
    }

    /**
     * Get the current state of the model version. The current state is derived from
     * logged events of the model version.
     *
     * @return The current state.
     */
    @JsonProperty(STATE)
    public ModelVersionState getState() {
        return events
            .stream()
            .sorted(Comparator
                .<ModelVersionEvent, Instant>comparing(e -> e
                    .getCreated()
                    .getAt())
                .reversed())
            .filter(event -> event instanceof StateChangedEvent)
            .map(event -> (StateChangedEvent) event)
            .map(StateChangedEvent::getState)
            .findFirst()
            .orElse(ModelVersionState.REGISTERED);
    }


    /**
     * Create a new instance of the model version with a new logged event.
     *
     * @param event The event which has happened.
     * @return A new instance.
     */
    public ModelVersion withEvent(ModelVersionEvent event) {
        var events = Stream
            .concat(Stream.of(event), this.events.stream())
            .collect(Collectors.toList());
        return this.withEvents(events);
    }

}
