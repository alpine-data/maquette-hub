package maquette.core.entities.projects.model.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.entities.projects.model.model.actions.*;
import maquette.core.entities.projects.model.model.events.Approved;
import maquette.core.entities.projects.model.model.events.ModelVersionEvent;
import maquette.core.entities.projects.model.model.events.Registered;
import maquette.core.entities.projects.model.model.events.StateChangedEvent;
import maquette.core.entities.projects.model.model.governance.CodeQuality;
import maquette.core.entities.projects.model.model.governance.DataDependencies;
import maquette.core.entities.projects.model.model.governance.GitDetails;
import maquette.core.entities.projects.model.questionnaire.Questionnaire;
import maquette.core.values.ActionMetadata;

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

   public static ModelVersion apply(
      String version, String description, ActionMetadata registered, Set<String> flavours, String stage, Questionnaire questionnaire) {
      return apply(
         version, description, registered, registered, flavours, stage,
         questionnaire, null, null, null, List.of(Registered.apply(registered)));
   }

   @JsonProperty("actions")
   public List<ModelAction> getActions() {
      var actions = Lists.<ModelAction>newArrayList();

      if (questionnaire.getAnswers().isEmpty()) {
         actions.add(FillQuestionnaire.apply());
      } else {
         actions.add(ReviewQuestionnaire.apply());
      }

      if (getApproved().isEmpty() && questionnaire.getAnswers().isPresent()) {
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

   public Optional<DataDependencies> getDataDependencies() {
      return Optional.ofNullable(dataDependencies);
   }

   public Optional<GitDetails> getGitDetails() {
      return Optional.ofNullable(gitDetails);
   }

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

   public ModelVersion withEvent(ModelVersionEvent event) {
      // TODO: Check state / invalid transitions

      var events = Stream.concat(Stream.of(event), this.events.stream()).collect(Collectors.toList());
      return this.withEvents(events);
   }

}
