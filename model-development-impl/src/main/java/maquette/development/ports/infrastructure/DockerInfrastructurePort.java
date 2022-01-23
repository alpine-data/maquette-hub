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
import maquette.development.ports.infrastructure.docker.Docker;
import maquette.development.ports.infrastructure.docker.deployments.MlflowStackDeployment;
import maquette.development.ports.infrastructure.docker.deployments.StackDeployment;
import maquette.development.ports.infrastructure.docker.deployments.StackDeploymentList;
import maquette.development.values.exceptions.StackConfigurationNotFoundException;
import maquette.development.values.stacks.MlflowStackConfiguration;
import maquette.development.values.stacks.StackConfiguration;
import maquette.development.values.stacks.StackInstanceParameters;
import maquette.development.values.stacks.StackInstanceStatus;
import org.apache.commons.lang3.SystemUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor(staticName = "apply")
public final class DockerInfrastructurePort implements InfrastructurePort {

    private final Docker docker;

    private final Path deploymentConfigurationStore;

    private final ObjectMapper om;

    public static DockerInfrastructurePort apply() {
        var om = DefaultObjectMapperFactory.apply().createJsonMapper(true);
        var path = SystemUtils.getUserHome().toPath().resolve(".mq").resolve("docker");
        var docker = Docker.apply(path, om);
        var deploymentConfigurationStore = path.resolve("deployments.json");

        return apply(docker, deploymentConfigurationStore, om);
    }

    @Override
    public CompletionStage<Done> createOrUpdateStackInstance(UID workspace, StackConfiguration configuration) {
        if (configuration instanceof MlflowStackConfiguration) {
            return createOrUpdateMlflow(workspace, (MlflowStackConfiguration) configuration);
        } else {
            return CompletableFuture.failedFuture(new RuntimeException(
                String.format("Unknown stack type `%s`", configuration.getStackInstanceName())));
        }
    }

    @Override
    public CompletionStage<Done> removeStackInstance(String name) {
        // TODO mw
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<Done> checkState() {
        return CompletableFuture.supplyAsync(() -> {
            getDeployedStackConfigurations().forEach(deployed -> {
                docker.runDeployment(deployed.getDeploymentConfig());
            });

            return Done.getInstance();
        });
    }

    @Override
    public CompletionStage<StackInstanceParameters> getInstanceParameters(UID workspace, String name) {
        var parameters = Maps.<String, String>newHashMap();
        var deployedStackConfiguration = getDeployedStackConfiguration(name);

        return docker
            .runDeployment(deployedStackConfiguration.getDeploymentConfig())
            .thenCompose(deployedStackConfiguration::getInstanceParameters);
    }

    @Override
    public CompletionStage<StackInstanceStatus> getStackInstanceStatus(String name) {
        return findDeployedStackConfiguration(name)
            .map(config -> docker
                .runDeployment(config.getDeploymentConfig())
                .thenApply(deployment -> StackInstanceStatus.DEPLOYED))
            .orElse(CompletableFuture.completedFuture(StackInstanceStatus.FAILED));
    }

    private CompletionStage<Done> createOrUpdateMlflow(UID workspace, MlflowStackConfiguration configuration) {
        insertOrUpdate(MlflowStackDeployment.apply(configuration));
        return getStackInstanceStatus(configuration.getStackInstanceName()).thenApply(i -> Done.getInstance());
    }

    private void insertOrUpdate(StackDeployment configuration) {
        var current = getDeployedStackConfigurations()
            .stream()
            .filter(config -> !config.getStackConfiguration()
                .getStackInstanceName()
                .equals(configuration.getStackConfiguration().getStackInstanceName()));

        var updated = Streams
            .concat(current, Stream.of(configuration))
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
        return findDeployedStackConfiguration(stackInstanceName).orElseThrow(() -> StackConfigurationNotFoundException.applyFromId(UID.apply(stackInstanceName)));
    }

    private Optional<StackDeployment> findDeployedStackConfiguration(String stackInstanceName) {
        return getDeployedStackConfigurations()
            .stream()
            .filter(config -> config.getStackConfiguration().getStackInstanceName().equals(stackInstanceName))
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

}
