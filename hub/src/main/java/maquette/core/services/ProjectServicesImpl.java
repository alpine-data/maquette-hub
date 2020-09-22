package maquette.core.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.entities.infrastructure.InfrastructureManager;
import maquette.core.entities.infrastructure.model.ContainerConfig;
import maquette.core.entities.infrastructure.model.DeploymentConfig;
import maquette.core.entities.processes.ProcessManager;
import maquette.core.entities.project.model.ProjectSummary;
import maquette.core.values.user.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class ProjectServicesImpl implements ProjectServices {

    ProcessManager processManager;

    maquette.core.entities.project.Projects projects;

    InfrastructureManager infrastructure;

    @Override
    public CompletionStage<Integer> create(User executor, String name) {
        return projects
                .createProject(executor, name)
                .thenCompose(projectId -> {
                    var processDescription = String.format("initialize project `%s`", name);
                    return processManager.schedule(executor, processDescription, log -> {
                        var deploymentName = String.format("%s__nginx", projectId);
                        var deploymentConfig = DeploymentConfig.apply(deploymentName, ContainerConfig.apply(deploymentName, "nginxdemos/hello"));

                        log.debug("Deploying %s ...", deploymentName);

                        return infrastructure
                                .applyConfig(deploymentConfig)
                                .thenApply(done -> {
                                    log.debug("Finished deployment of %", deploymentName);
                                    return done;
                                });
                    });
                });
    }

    @Override
    public CompletionStage<List<ProjectSummary>> list(User user) {
        return projects.getProjects();
    }

    @Override
    public CompletionStage<Done> remove(User user, String name) {
        return CompletableFuture.completedFuture(Done.getInstance());
    }

}
