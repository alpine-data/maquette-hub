package maquette.core.values.questionnaire;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.ActionMetadata;

@Value
@AllArgsConstructor(staticName = "apply")
public class Answers {

   ActionMetadata answered;

   JsonNode responses;

}
