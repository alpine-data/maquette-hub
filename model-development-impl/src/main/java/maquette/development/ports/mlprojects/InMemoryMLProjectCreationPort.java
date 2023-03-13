package maquette.development.ports.mlprojects;

import lombok.AllArgsConstructor;
import maquette.development.values.mlproject.MLProjectType;
import maquette.development.values.mlproject.MachineLearningProject;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public class InMemoryMLProjectCreationPort implements MLProjectCreationPort {
    @Override
    public CompletionStage<MachineLearningProject> createMachineLearningProject(String workspaceName, String projectName, MLProjectType templateType) {
        return CompletableFuture.completedFuture(
            MachineLearningProject.apply(
                projectName,
                "http://dev.azure.com/organisation/_project/_git/" + projectName,
                "git://dev.azure.com/organisation/_project/_git/" + projectName,
                "http://backstage.com/catalog/components/" + projectName
            )
        );
    }
}
