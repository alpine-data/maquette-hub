package maquette.development.ports.infrastructure;

import akka.Done;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.databind.DefaultObjectMapperFactory;
import maquette.core.values.UID;
import maquette.development.ports.infrastructure.docker.Deployment;
import maquette.development.ports.infrastructure.docker.Docker;
import maquette.development.ports.infrastructure.docker.deployments.*;
import maquette.development.values.exceptions.StackConfigurationNotFoundException;
import maquette.development.values.stacks.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor(staticName = "apply")
public final class DockerInfrastructurePort implements InfrastructurePort {

    private static final Logger LOG = LoggerFactory.getLogger(DockerInfrastructurePort.class);

    private final Docker docker;

    private final Path deploymentConfigurationStore;

    private final ObjectMapper om;

    private final Map<String, Deployment> deployments;

    public static DockerInfrastructurePort apply() {
        var om = DefaultObjectMapperFactory
            .apply()
            .createJsonMapper(true);
        var path = SystemUtils
            .getUserHome()
            .toPath()
            .resolve(".mq")
            .resolve("docker");
        var docker = Docker.apply(path, om);
        var deploymentConfigurationStore = path.resolve("deployments.json");

        var inst = apply(docker, deploymentConfigurationStore, om, Maps.newHashMap());
        Runtime.getRuntime().addShutdownHook(new Thread(inst::clean));
        return inst;
    }

    @Override
    public CompletionStage<Done> createOrUpdateStackInstance(UID workspace, StackConfiguration configuration) {
        deployments.remove(configuration.getStackInstanceName());

        if (configuration instanceof MlflowStackConfiguration) {
            return createOrUpdateMlflow(workspace, (MlflowStackConfiguration) configuration);
        } else if (configuration instanceof PythonStackConfiguration) {
            return createOrUpdatePython(workspace, (PythonStackConfiguration) configuration);
        } else if (configuration instanceof MLWorkspaceStackConfiguration) {
            return createOrUpdateMLWorkspace(workspace, (MLWorkspaceStackConfiguration) configuration);
        } else {
            return CompletableFuture.failedFuture(new RuntimeException(
                String.format("Unknown stack type `%s`", configuration
                    .getClass()
                    .getName())));
        }
    }

    @Override
    public CompletionStage<Done> removeStackInstance(String name) {
        if (deployments.containsKey(name)) {
            return deployments
                .get(name)
                .remove()
                .thenApply(done -> {
                    deployments.remove(name);
                    removeDeployedStackConfiguration(name);
                    return done;
                });
        } else {
            return docker
                .runDeployment(getDeployedStackConfiguration(name).getDeploymentConfig())
                .thenCompose(Deployment::remove)
                .thenApply(done -> {
                    deployments.remove(name);
                    removeDeployedStackConfiguration(name);
                    return done;
                });
        }
    }

    @Override
    public CompletionStage<Done> checkState(boolean forceUpdate) {
        return CompletableFuture.supplyAsync(() -> {
            getDeployedStackConfigurations().forEach(deployed -> docker.runDeployment(deployed.getDeploymentConfig()));
            return Done.getInstance();
        });
    }

    @Override
    public CompletionStage<StackInstanceParameters> getInstanceParameters(UID workspace, String name) {
        var deployedStackConfiguration = getDeployedStackConfiguration(name);

        if (deployments.containsKey(name)) {
            return deployedStackConfiguration.getInstanceParameters(deployments.get(name));
        } else {
            return docker
                .runDeployment(deployedStackConfiguration.getDeploymentConfig())
                .thenCompose(deployedStackConfiguration::getInstanceParameters);
        }
    }

    @Override
    public CompletionStage<StackInstanceStatus> getStackInstanceStatus(String name) {
        if (deployments.containsKey(name)) {
            return CompletableFuture.completedFuture(StackInstanceStatus.DEPLOYED);
        } else {
            return findDeployedStackConfiguration(name)
                .map(config -> docker
                    .runDeployment(config.getDeploymentConfig())
                    .thenApply(deployment -> {
                        this.deployments.put(name, deployment);
                        return StackInstanceStatus.DEPLOYED;
                    }))
                .orElse(CompletableFuture.completedFuture(StackInstanceStatus.FAILED));
        }
    }

    public void clean() {
        LOG.info("Cleaning local infrastructure ...");
        Operators.ignoreExceptions(() -> FileUtils.forceDelete(this.deploymentConfigurationStore.toFile()), LOG);

        this.docker.cleanDockerResources();
    }

    private CompletionStage<Done> createOrUpdateMlflow(UID workspace, MlflowStackConfiguration configuration) {
        insertOrUpdate(MlflowStackDeployment.apply(configuration));
        return getStackInstanceStatus(configuration.getStackInstanceName()).thenApply(i -> Done.getInstance());
    }

    private CompletionStage<Done> createOrUpdatePython(UID workspace, PythonStackConfiguration configuration) {
        insertOrUpdate(PythonStackDeployment.apply(configuration));
        return getStackInstanceStatus(configuration.getStackInstanceName()).thenApply(i -> Done.getInstance());
    }

    private CompletionStage<Done> createOrUpdateMLWorkspace(UID workspace, MLWorkspaceStackConfiguration configuration) {
        insertOrUpdate(MLWorkspaceDeployment.apply(configuration));
        return getStackInstanceStatus(configuration.getStackInstanceName()).thenApply(i -> Done.getInstance());
    }

    private void insertOrUpdate(StackDeployment configuration) {
        var current = getDeployedStackConfigurations()
            .stream()
            .filter(config -> !config
                .getStackConfiguration()
                .getStackInstanceName()
                .equals(configuration
                    .getStackConfiguration()
                    .getStackInstanceName()));

        var updated = Streams
            .concat(current, Stream.of(configuration))
            .collect(Collectors.toList());

        updateDeployedStackConfigurations(updated);
    }

    private void removeDeployedStackConfiguration(String name) {
        var updated = getDeployedStackConfigurations()
            .stream()
            .filter(d -> !d
                .getStackInstanceName()
                .equals(name))
            .collect(Collectors.toList());

        updateDeployedStackConfigurations(updated);
    }

    private void updateDeployedStackConfigurations(List<StackDeployment> configurations) {
        Operators.suppressExceptions(() -> {
            Files.createDirectories(deploymentConfigurationStore.getParent());
            om.writeValue(deploymentConfigurationStore.toFile(), StackDeploymentList.apply(configurations));
        });
    }

    private StackDeployment getDeployedStackConfiguration(String stackInstanceName) {
        return findDeployedStackConfiguration(stackInstanceName).orElseThrow(
            () -> StackConfigurationNotFoundException.applyFromId(UID.apply(stackInstanceName)));
    }

    private Optional<StackDeployment> findDeployedStackConfiguration(String stackInstanceName) {
        return getDeployedStackConfigurations()
            .stream()
            .filter(config -> config
                .getStackConfiguration()
                .getStackInstanceName()
                .equals(stackInstanceName))
            .findFirst();
    }

    private List<StackDeployment> getDeployedStackConfigurations() {
        if (Files.exists(deploymentConfigurationStore)) {
            return Operators.suppressExceptions(() -> om.readValue(
                deploymentConfigurationStore.toFile(),
                new TypeReference<>() {
                }));
        } else {
            return Lists.newArrayList();
        }
    }

    @Override
    public CompletionStage<Done> importModel(UID sourceWorkspace, String sourceModelName, String sourceModelVersion,
                                             UID destinationWorkspace, String destinationModelName,
                                             String destinationExperimentId, Map<String, String> sourceEnv,
                                             Map<String, String> destinationEnv) {
        return CompletableFuture.completedFuture(Done.getInstance());
    }
}
