package maquette.development.services;

import akka.Done;
import akka.japi.Pair;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.modules.users.UserEntities;
import maquette.core.modules.users.UserEntity;
import maquette.core.modules.users.model.UserAuthenticationToken;
import maquette.core.modules.users.services.UserCompanion;
import maquette.core.values.UID;
import maquette.core.values.user.User;
import maquette.development.entities.SandboxEntities;
import maquette.development.entities.SandboxEntity;
import maquette.development.entities.WorkspaceEntities;
import maquette.development.entities.WorkspaceEntity;
import maquette.development.values.EnvironmentType;
import maquette.development.values.exceptions.InvalidStackHashException;
import maquette.development.values.sandboxes.Sandbox;
import maquette.development.values.sandboxes.SandboxProperties;
import maquette.development.values.sandboxes.volumes.VolumeDefinition;
import maquette.development.values.stacks.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class SandboxServicesImpl implements SandboxServices {

    private final SandboxEntities sandboxes;

    private final WorkspaceEntities workspaces;

    private final UserEntities users;

    private final UserCompanion userCompanion;

    @Override
    public CompletionStage<SandboxProperties> createSandbox(
        User user, String workspace, String name, String comment, Optional<VolumeDefinition> volume,
        List<StackConfiguration> stacks) {

        return
            userCompanion
                .withUser(user)
                .thenCompose(UserEntity::getProfileById)
                .thenCompose((profile) ->
                    workspaces
                        .getWorkspaceByName(workspace)
                        .thenCompose(wks -> volume.map(v -> wks
                                .createVolume(user, v)
                                .thenApply(volumeConfiguration -> Pair.apply(wks, volumeConfiguration)))
                            .orElse(CompletableFuture.completedFuture(Pair.apply(wks, null))))
                        .thenCompose(pair -> {
                            var wks = pair.first();
                            var vol = Optional.ofNullable(pair.second());
                            return sandboxes.createSandbox(user, wks.getId(), vol.map(VolumeProperties::getId)
                                .orElse(null), name, comment);
                        })
                        .thenCompose(sdbx -> sandboxes.getSandboxById(sdbx.getWorkspace(), sdbx.getId()))
                        .thenCompose(sdbx -> sdbx
                            .getProperties()
                            .thenApply(prop -> sdbx.addStacks(stacks.stream().map(stack -> {
                                if (stack instanceof PythonStackConfiguration)
                                    return ((PythonStackConfiguration) stack).withUserEmail(profile.getEmail());
                                if (stack instanceof PythonGPUStackConfiguration)
                                    return ((PythonGPUStackConfiguration) stack).withUserEmail(profile.getEmail());
                                return stack;
                            }).collect(Collectors.toList()), prop))
                            .thenCompose(d -> sdbx.getProperties())));
    }

    @Override
    public CompletionStage<UserAuthenticationToken> getAuthenticationToken(
        UID workspaceId, UID sandboxId, String stackHash) {

        return sandboxes
            .getSandboxById(workspaceId, sandboxId)
            .thenCompose(sdbx -> {
                var parametersCS = sdbx.getStackInstanceParameters(EnvironmentType.SANDBOX);
                var userCS = sdbx
                    .getProperties()
                    .thenApply(properties -> properties
                        .getCreated()
                        .getBy())
                    .thenCompose(userId -> users.getUserById(UID.apply(userId)));

                return Operators
                    .compose(parametersCS, userCS, (parameters, user) -> {
                        var containsHash = false;

                        for (var stackInstanceName : parameters.keySet()) {
                            var p = parameters
                                .get(stackInstanceName)
                                .getParametersDecoded()
                                .getOrDefault(StackConfiguration.PARAM_STACK_TOKEN, null);

                            if (p != null && p.equals(stackHash)) {
                                containsHash = true;
                                break;
                            }
                        }

                        if (containsHash) {
                            return user.getAuthenticationToken();
                        } else {
                            return CompletableFuture.<UserAuthenticationToken>failedFuture(
                                InvalidStackHashException.apply(workspaceId, sandboxId, stackHash));
                        }
                    })
                    .thenCompose(cs -> cs);
            });
    }

    @Override
    public CompletionStage<Sandbox> getSandbox(User user, String workspace, String sandbox) {
        return withSandboxByName(workspace, sandbox, (wks, sdbx) -> {
            var propertiesCS = sdbx.getProperties();
            var stateCS = sdbx.getState();

            return Operators.compose(propertiesCS, stateCS, Sandbox::apply);
        });
    }

    @Override
    public CompletionStage<List<StackProperties>> getStacks(User user) {
        return CompletableFuture.completedFuture(Stacks
            .apply()
            .getStacks());
    }

    @Override
    public CompletionStage<List<SandboxProperties>> getSandboxes(User user, String workspace) {
        return workspaces
            .getWorkspaceByName(workspace)
            .thenCompose(wks -> sandboxes.listSandboxes(wks.getId()));
    }

    @Override
    public CompletionStage<Done> removeSandbox(User user, String workspace, String sandbox) {
        return withSandboxByName(workspace, sandbox, (wks, sdbx) -> sdbx.remove());
    }

    private <T> CompletionStage<T> withSandboxByName(String workspace, String sandbox, BiFunction<WorkspaceEntity,
        SandboxEntity, CompletionStage<T>> func) {
        return workspaces
            .getWorkspaceByName(workspace)
            .thenCompose(wks -> sandboxes
                .getSandboxByName(wks.getId(), sandbox)
                .thenCompose(sdbx -> func.apply(wks, sdbx)));
    }

}
