package maquette.development.entities.mlflow.explainer;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Interface to model recognized explainer artifacts stored in MLflow.
 *
 * Maquette may support multiple explainers/ model analysis results. These results are logged
 * in one or more files within an experiment. The information about these stored explainer files
 * are stored in the below-mentioned explainer objects.
 *
 * Overview of available explainers
 * --------------------------------
 *
 * {@link HtmlExplainerReport} to retrieve artifacts generated with Shapash (https://github.com/MAIF/shapash).
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type")
@JsonSubTypes(
    {
        @JsonSubTypes.Type(value = HtmlExplainerReport.class, name = "shapash"),
    })
public interface ExplainerArtifact {
}
