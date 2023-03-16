package maquette.development.ports.mlprojects;

import maquette.development.values.mlproject.MLProjectType;
import maquette.development.values.mlproject.MachineLearningProject;

import java.util.concurrent.CompletionStage;

/**
 * This port is used to trigger creation of new projects on the companies application development portal,
 * e.g. via Backstage or Azure DevOps.
 */
public interface MLProjectCreationPort {

    CompletionStage<MachineLearningProject> createMachineLearningProject(
        String workspaceName,
        String projectName,
        MLProjectType templateType);

}
