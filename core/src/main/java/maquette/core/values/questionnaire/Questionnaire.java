package maquette.core.values.questionnaire;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

import java.util.Optional;


@With
@Value
@AllArgsConstructor(staticName = "apply")
public class Questionnaire {

    JsonNode questions;

    Answers answers;

    public static Questionnaire apply(JsonNode questions) {
        return apply(questions, null);
    }

    public Optional<Answers> getAnswers() {
        return Optional.ofNullable(answers);
    }

}
