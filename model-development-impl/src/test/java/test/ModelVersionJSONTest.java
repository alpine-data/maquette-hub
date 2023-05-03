package test;

import com.fasterxml.jackson.core.JsonProcessingException;
import maquette.core.databind.DefaultObjectMapperFactory;
import maquette.core.values.ActionMetadata;
import maquette.development.values.model.ModelVersion;
import maquette.development.values.model.ModelVersionStage;
import maquette.development.values.model.events.ReviewRequested;
import maquette.development.values.model.governance.CodeQuality;
import maquette.development.values.model.governance.DataDependencies;
import maquette.development.values.model.governance.GitDetails;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public class ModelVersionJSONTest {

    @Test
    public void testSerialize() throws JsonProcessingException {
        var version = ModelVersion.apply(
            "1.0.0",
            ActionMetadata.apply("egon"),
            ActionMetadata.apply("olsen"),

            Set.of("pythin"),
            ModelVersionStage.STAGING,
            CodeQuality.apply(Instant.now(), "foo", 10, 10, List.of()),
            GitDetails.apply("foo", "foo", false),
            DataDependencies.apply(Instant.now(), List.of()),
            List.of(),
            List.of(ReviewRequested.apply(ActionMetadata.apply("egon"))),
            "bvlassaf"
        );

        var om = DefaultObjectMapperFactory.apply().createJsonMapper(true);
        var json = om.writeValueAsString(version);
        System.out.println(json);

        var json2 = "{\n" +
            "  \"version\" : \"1.0.0\",\n" +
            "  \"registered\" : {\n" +
            "    \"by\" : \"egon\",\n" +
            "    \"at\" : \"2023-05-03T09:27:43.445167Z\"\n" +
            "  },\n" +
            "  \"updated\" : {\n" +
            "    \"by\" : \"olsen\",\n" +
            "    \"at\" : \"2023-05-03T09:27:43.445197Z\"\n" +
            "  },\n" +
            "  \"flavours\" : [ \"pythin\" ],\n" +
            "  \"stage\" : \"Staging\",\n" +
            "  \"codeQuality\" : {\n" +
            "    \"CHECKED\" : \"2023-05-03T09:27:43.451062Z\",\n" +
            "    \"COMMIT\" : \"foo\",\n" +
            "    \"SCORE\" : 10,\n" +
            "    \"TEST_COVERAGE\" : 10,\n" +
            "    \"ISSUES\" : [ ]\n" +
            "  },\n" +
            "  \"gitDetails\" : {\n" +
            "    \"commit\" : \"foo\",\n" +
            "    \"transferUrl\" : \"foo\",\n" +
            "    \"isMainBranch\" : false\n" +
            "  },\n" +
            "  \"dataDependencies\" : {\n" +
            "    \"checked\" : \"2023-05-03T09:27:43.454482Z\",\n" +
            "    \"assets\" : [ ]\n" +
            "  },\n" +
            "  \"events\" : [ {\n" +
            "    \"event\" : \"review-requested\",\n" +
            "    \"created\" : {\n" +
            "      \"by\" : \"egon\",\n" +
            "      \"at\" : \"2023-05-03T09:27:43.455873Z\"\n" +
            "    }\n" +
            "  } ],\n" +
            "  \"runId\" : \"bvlassaf\",\n" +
            "  \"codeQualityChecks\" : [ {\n" +
            "    \"type\" : \"ok\",\n" +
            "    \"message\" : \"Code is tracked with Git. The commit is `foo`\"\n" +
            "  }, {\n" +
            "    \"type\" : \"ok\",\n" +
            "    \"message\" : \"Code is tracked at `foo`\"\n" +
            "  }, {\n" +
            "    \"type\" : \"exemption\",\n" +
            "    \"message\" : \"Code is not merged in main branch\"\n" +
            "  }, {\n" +
            "    \"type\" : \"ok\",\n" +
            "    \"message\" : \"Code quality is tracked.\"\n" +
            "  }, {\n" +
            "    \"type\" : \"ok\",\n" +
            "    \"message\" : \"No critical code quality issues\"\n" +
            "  }, {\n" +
            "    \"type\" : \"ok\",\n" +
            "    \"message\" : \"No minor code quality issues\"\n" +
            "  }, {\n" +
            "    \"type\" : \"warning\",\n" +
            "    \"message\" : \"Test coverage is below 60% - Currently 10%\"\n" +
            "  } ],\n" +
            "  \"codeQualitySummary\" : \"1 exceptions, 1 warnings\",\n" +
            "  \"dataDependencyChecks\" : [ {\n" +
            "    \"type\" : \"ok\",\n" +
            "    \"message\" : \"Data dependencies tracked\"\n" +
            "  } ],\n" +
            "  \"dataDependencySummary\" : \"Homer Simpson says \\\"We'll just sit back, relax, and enjoy the sweet " +
            "taste of success. Woo hoo!\\\"\",\n" +
            "  \"actions\" : [ ],\n" +
            "  \"state\" : \"requested\"\n" +
            "}";


        System.out.println(om.readValue(json2, ModelVersion.class));
    }

}
