package maquette.development.values.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.ActionMetadata;
import maquette.core.values.questionnaire.Questionnaire;
import maquette.development.values.model.actions.*;
import maquette.development.values.model.events.Approved;
import maquette.development.values.model.events.ModelVersionEvent;
import maquette.development.values.model.events.Registered;
import maquette.development.values.model.events.StateChangedEvent;
import maquette.development.values.model.governance.*;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@With
@Value
@AllArgsConstructor(staticName = "apply")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelVersion {

   String version;

   String description;

   ActionMetadata registered;

   ActionMetadata updated;

   Set<String> flavours;

   String stage;

   Questionnaire questionnaire;

   CodeQuality codeQuality;

   GitDetails gitDetails;

   DataDependencies dataDependencies;

   List<ModelVersionEvent> events;

   ModelExplainer explainer;

   public static ModelVersion apply(
      String version, String description, ActionMetadata registered, Set<String> flavours, String stage, Questionnaire questionnaire) {
      return apply(
         version, description, registered, registered, flavours, stage,
         questionnaire, null, null, null, List.of(Registered.apply(registered)), null);
   }

   @JsonProperty("actions")
   public List<ModelAction> getActions() {
      var actions = Lists.<ModelAction>newArrayList();

      if (questionnaire.getAnswers().isEmpty()) {
         actions.add(FillQuestionnaire.apply());
      } else {
         actions.add(ReviewQuestionnaire.apply());
      }

      if (getApproved().isEmpty() && questionnaire.getAnswers().isPresent() && !getState().equals(ModelVersionState.REVIEW_REQUESTED)) {
         actions.add(RequestReview.apply());
      }

      if (getState().equals(ModelVersionState.REVIEW_REQUESTED)) {
         actions.add(ApproveModel.apply());
      }

      getGitDetails()
         .flatMap(GitDetails::getTransferUrl)
         .ifPresent(url -> actions.add(ViewSource.apply(url)));

      if (!stage.equals("Archived")) {
         if (stage.equals("None")) {
            actions.add(PromoteModel.apply("Staging"));
         }

         if ((stage.equals("Staging") || stage.equals("None")) && getApproved().isPresent()) {
            actions.add(PromoteModel.apply("Production"));
         }
      }

      if (!stage.equals("Archived")) {
         actions.add(ArchiveModel.apply());
      } else {
         actions.add(RestoreModel.apply());
      }

      return actions;
   }

   @JsonProperty("actions")
   @SuppressWarnings("unused")
   private void setActions(List<ModelAction> actions) {
      // ignore
   }

   public Optional<ActionMetadata> getApproved() {
      return events
         .stream()
         .filter(event -> event instanceof Approved)
         .sorted(Comparator.<ModelVersionEvent, Instant>comparing(e -> e.getCreated().getAt()).reversed())
         .map(ModelVersionEvent::getCreated)
         .findFirst();
   }

   public Optional<CodeQuality> getCodeQuality() { return Optional.ofNullable(codeQuality); }

   @JsonProperty("codeQualityChecks")
   public List<CheckResult> getCodeQualityChecks() {
      List<CheckResult> result = Lists.newArrayList();

      if (gitDetails == null || gitDetails.getCommit().isEmpty()) {
         result.add(CheckExemption.apply("Code is not tracked with Git"));
      } else {
         result.add(CheckOk.apply(String.format("Code is tracked with Git. The commit is `%s`", gitDetails.getCommit().get())));
      }

      if (gitDetails == null || gitDetails.getTransferUrl().isEmpty()) {
         result.add(CheckExemption.apply("Code is not tracked with central Git repository"));
      } else {
         result.add(CheckOk.apply(String.format("Code is tracked at `%s`", gitDetails.getTransferUrl().get())));
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
            .filter(i -> i.getType().equals(IssueType.CRITICAL))
            .count();

         var warnings = codeQuality
            .getIssues()
            .stream()
            .filter(i -> i.getType().equals(IssueType.WARNING))
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
            result.add(CheckWarning.apply(String.format("Test coverage is below 60%% - Currently %d%%", codeQuality.getTestCoverage())));
         } else {
            result.add(CheckOk.apply(String.format("Test coverage of %s%%", codeQuality.getTestCoverage())));
         }
      }

      return result;
   }

   @SuppressWarnings("unused")
   @JsonProperty("codeQualityChecks")
   private void setCodeQualityChecks(List<CheckResult> value) {
      // ignore
   }

   @JsonProperty("codeQualitySummary")
   public String getCodeQualitySummary() {
      var checks = getCodeQualityChecks();
      var exceptions = checks.stream().filter(r -> r instanceof CheckExemption).count();
      var warnings = checks.stream().filter(r -> r instanceof CheckWarning).count();

      if (exceptions > 0) {
         return String.format("%d exceptions, %d warnings", exceptions, warnings);
      } else if (warnings > 0) {
         return String.format("%d warnings", warnings);
      } else {
         return "Diddly doodly fine";
      }
   }

   public Optional<DataDependencies> getDataDependencies() {
      return Optional.ofNullable(dataDependencies);
   }

   @JsonProperty("dataDependencyChecks")
   public List<CheckResult> getDataDependencyChecks() {
      List<CheckResult> result = Lists.newArrayList();

      if (dataDependencies == null) {
         result.add(CheckWarning.apply("Data dependencies not tracked."));
      } else {
         result.add(CheckOk.apply("Data dependencies tracked"));

         if (this.isDependantOnSensitivePersonalInformation()) {
            result.add(CheckWarning.apply("The model depends on sensitive personal information"));
         }

         if (this.isDependantOnPersonalInformation()) {
            result.add(CheckWarning.apply("The model depends on personal information"));
         }

         if (this.isDependantOnRawData()) {
            result.add(CheckWarning.apply("The model depends on raw data sets"));
         }
      }

      return result;
   }

   @JsonProperty("dataDependencyChecks")
   @SuppressWarnings("unused")
   private void setDataDependenciesChecks(List<CheckResult> value) {
      // ignore
   }

   @JsonProperty("dataDependencySummary")
   public String getDataDependencySummary() {
      var checks = getDataDependencyChecks();
      var exceptions = checks.stream().filter(r -> r instanceof CheckExemption).count();
      var warnings = checks.stream().filter(r -> r instanceof CheckWarning).count();

      if (exceptions > 0) {
         return String.format("%d exceptions, %d warnings", exceptions, warnings);
      } else if (warnings > 0) {
         return String.format("%d warnings", warnings);
      } else {
         return "Okily Dokily!";
      }
   }

   @JsonProperty("dataGovernanceSummary")
   public String getDataGovernanceSummary() {
      var state = getState();

      if (questionnaire == null) {
         return "Questionnaire not filled";
      } else if (state.equals(ModelVersionState.REVIEW_REQUESTED)) {
         return "Review requested";
      } else if (state.equals(ModelVersionState.REJECTED)) {
         return "Model rejected";
      } else if (state.equals(ModelVersionState.APPROVED)) {
         return "Model is approved";
      } else {
         return "Requires review";
      }
   }

   public Optional<GitDetails> getGitDetails() {
      return Optional.ofNullable(gitDetails);
   }

   public Optional<ModelExplainer> getExplainer() {
      return Optional.ofNullable(explainer);
   }

   @JsonProperty("state")
   public ModelVersionState getState() {
      return events
         .stream()
         .sorted(Comparator.<ModelVersionEvent, Instant>comparing(e -> e.getCreated().getAt()).reversed())
         .filter(event -> event instanceof StateChangedEvent)
         .map(event -> (StateChangedEvent) event)
         .map(StateChangedEvent::getState)
         .findFirst()
         .orElse(ModelVersionState.REGISTERED);
   }

   @JsonProperty("isDependantOnPI")
   public boolean isDependantOnPersonalInformation() {
      if (dataDependencies == null) {
         return false;
      } else {
         // TODO mw: Implement Lookup.
         return false;
      }
   }

   @JsonProperty("isDependantOnRawData")
   public boolean isDependantOnRawData() {
      // TODO mw: Implement Lookup.
      if (dataDependencies == null) {
         return false;
      } else {
         return false;
      }
   }

   @JsonProperty("isDependantOnSPI")
   public boolean isDependantOnSensitivePersonalInformation() {
      // TODO mw: Implement Lookup.
      if (dataDependencies == null) {
         return false;
      } else {
         return false;
      }
   }

   public ModelVersion withEvent(ModelVersionEvent event) {
      // TODO mw: Check state / invalid transitions

      var events = Stream.concat(Stream.of(event), this.events.stream()).collect(Collectors.toList());
      return this.withEvents(events);
   }

}
