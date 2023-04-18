package maquette.development.specs.steps;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import maquette.core.MaquetteRuntime;
import maquette.core.modules.users.GlobalRole;
import maquette.core.modules.users.commands.GrantGlobalRoleCommand;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;
import maquette.development.MaquetteModelDevelopment;
import maquette.development.commands.*;
import maquette.development.commands.applications.*;
import maquette.development.commands.members.GrantWorkspaceMemberCommand;
import maquette.development.commands.members.RevokeWorkspaceMemberCommand;
import maquette.development.commands.registry.ImportToRegistryCommand;
import maquette.development.commands.sandboxes.CreateSandboxCommand;
import maquette.development.commands.sandboxes.GetSandboxCommand;
import maquette.development.commands.sandboxes.RemoveSandboxCommand;
import maquette.development.values.EnvironmentType;
import maquette.development.values.WorkspaceMemberRole;
import maquette.development.values.sandboxes.volumes.ExistingVolume;
import maquette.development.values.sandboxes.volumes.NewVolume;
import maquette.development.values.sandboxes.volumes.VolumeDefinition;
import maquette.development.values.stacks.PythonGPUStackConfiguration;
import maquette.development.values.stacks.PythonStackConfiguration;
import maquette.development.values.stacks.VolumeProperties;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@AllArgsConstructor()
@Slf4j
public class WorkspaceStepDefinitions {

    protected final MaquetteRuntime runtime;

    protected final List<String> results;

    protected String mentionedWorkspace;

    private List<VolumeProperties> mentionedVolumes;

    protected Exception exception;

    public WorkspaceStepDefinitions(MaquetteRuntime runtime) {
        this(runtime, Lists.newArrayList(), null, Lists.newArrayList(), null);
    }

    public void $_browses_all_workspaces(AuthenticatedUser user) throws ExecutionException, InterruptedException {
        var result = ListWorkspacesCommand
            .apply()
            .run(user, runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(runtime);

        results.add(result);
    }

    public void $_creates_a_workspace_with_name_$(AuthenticatedUser user,
                                                  String workspaceName) throws ExecutionException,
        InterruptedException {
        var result = CreateWorkspaceCommand
            .apply(workspaceName, "fake-title", "fake-summary")
            .run(user, runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(runtime);

        mentionedWorkspace = workspaceName;
        results.add(result);
    }

    public void $_updates_a_workspace_with_name_$_to_a_new_name_$(AuthenticatedUser user, String workspaceName,
                                                                  String newWorkspaceName) throws ExecutionException,
        InterruptedException {
        var result = UpdateWorkspaceCommand
            .apply(workspaceName, newWorkspaceName, "fake-title", "fake-summary")
            .run(user, runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(runtime);

        results.add(result);
    }

    public void $_gets_workspace_with_name_$(AuthenticatedUser user,
                                             String workspaceName) throws ExecutionException, InterruptedException {
        var result = GetWorkspaceCommand
            .apply(workspaceName)
            .run(user, runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(runtime);

        results.add(result);

        mentionedVolumes = this.runtime
            .getModule(MaquetteModelDevelopment.class)
            .getWorkspaceServices()
            .getVolumes(user, workspaceName)
            .toCompletableFuture()
            .get();
    }

    public void $_grants_$_access_to_the_$_workspace_for_$(User executor, WorkspaceMemberRole memberRole,
                                                           String workspaceName,
                                                           AuthenticatedUser grantedUser) throws ExecutionException,
        InterruptedException {
        var result = GrantWorkspaceMemberCommand
            .apply(workspaceName, grantedUser
                .toAuthorization()
                .toGenericAuthorizationDefinition(), memberRole)
            .run(executor, runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(runtime);

        results.add(result);
    }

    public void $_revoke_access_to_the_$_workspace_for_$(User executor, String workspaceName,
                                                         AuthenticatedUser grantedUser) throws ExecutionException,
        InterruptedException {
        var result = RevokeWorkspaceMemberCommand
            .apply(workspaceName, grantedUser
                .toAuthorization()
                .toGenericAuthorizationDefinition())
            .run(executor, runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(runtime);

        results.add(result);
    }

    public void $_gets_environment_for_workspace_$_of_type_$(AuthenticatedUser user, String workspaceName,
                                                             EnvironmentType environmentType) throws ExecutionException, InterruptedException {
        var result = GetWorkspaceEnvironmentCommand
            .apply(workspaceName, environmentType)
            .run(user, runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(runtime);

        results.add(result);
    }

    public void $_configures_workspace_$_to_be_$(AuthenticatedUser user, String workspaceName, String newWorkspaceName,
                                                 String title,
                                                 String summary) throws ExecutionException, InterruptedException {
        var result = UpdateWorkspaceCommand
            .apply(workspaceName, newWorkspaceName, title, summary)
            .run(user, runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(runtime);

        results.add(result);
    }

    public void the_output_should_contain(String... queries) {
        var result = results.get(results.size() - 1);

        for (var q : queries) {
            assertThat(result).contains(q);
        }
    }

    public void the_output_should_be(String output) {
        var result = results.get(results.size() - 1);

        assertThat(result).isEqualTo(output);
    }

    public void the_output_should_not_contain(String... queries) {
        var result = results.get(results.size() - 1);

        for (var q : queries) {
            assertThat(result).doesNotContain(q);
        }
    }

    public void $_removes_workspace_with_name_$(AuthenticatedUser user,
                                                String workspaceName) throws ExecutionException, InterruptedException {
        var result = RemoveWorkspaceCommand
            .apply(workspaceName)
            .run(user, runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(runtime);

        results.add(result);
    }

    public void $_creates_a_$_sandbox_with_a_$_volume_named_$(AuthenticatedUser user, String sandboxName,
                                                              String newOrExisting,
                                                              String volumeName) {
        try {
            VolumeDefinition volume;
            if (newOrExisting.equalsIgnoreCase("new")) {
                volume = NewVolume.apply(volumeName);
            } else {
                volume = ExistingVolume.apply(volumeName);
            }
            var result = CreateSandboxCommand
                .apply(mentionedWorkspace, sandboxName, sandboxName, volume, List.of(
                    PythonStackConfiguration.apply(sandboxName, Lists.newArrayList(), "4Gi", "3.8",
                        "john.doe@zurich.ch",
                        Maps.<String, String>newHashMap())))
                .run(user, runtime)
                .toCompletableFuture()
                .get()
                .toPlainText(runtime);

            results.add(result);
        } catch (Exception e) {
            log.error("Error", e);
            this.exception = e;
        }
    }

    public void the_output_has_exactly_$_volume(int size) {
        assertThat(mentionedVolumes).hasSize(size);
    }

    public void $_gets_$_sandbox(AuthenticatedUser user, String sandboxName) throws ExecutionException,
        InterruptedException {
        var result = GetSandboxCommand
            .apply(mentionedWorkspace, sandboxName)
            .run(user, runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(runtime);
        results.add(result);
    }

    public void $_removes_the_$_sandbox(AuthenticatedUser user, String sandboxName) throws ExecutionException,
        InterruptedException {
        var result = RemoveSandboxCommand
            .apply(mentionedWorkspace, sandboxName)
            .run(user, runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(runtime);
        results.add(result);
    }

    public void an_error_occurs_with_a_message_$(String message) {
        assertThat(this.exception.getMessage()).contains(message);
    }

    public void $_waits_until_the_mlflow_stack_is_deployed(AuthenticatedUser user) throws ExecutionException,
        InterruptedException {
        var retries = 10;
        while (retries > 0) {
            var result = GetWorkspaceCommand
                .apply(mentionedWorkspace)
                .run(user, runtime)
                .toCompletableFuture()
                .get()
                .toPlainText(runtime);
            if (!result.contains("initialized"))
                break;
            retries--;
            TimeUnit.SECONDS.sleep(5);
        }
        if (retries == 0)
            fail("MLflow stack hasn't been initialized");
    }

    public void $_grants_advanced_user_to_user_$(AuthenticatedUser executor, AuthenticatedUser user) throws ExecutionException, InterruptedException {
        GrantGlobalRoleCommand
            .apply(user.toAuthorization().toGenericAuthorizationDefinition(), GlobalRole.ADVANCED_USER)
            .run(executor, runtime)
            .toCompletableFuture()
            .get().toPlainText(runtime);
    }

    public void $_creates_a_sandbox_$_with_and_advanced_stack(AuthenticatedUser user, String sandboxName) throws ExecutionException, InterruptedException {
        results.add(CreateSandboxCommand
            .apply(mentionedWorkspace, sandboxName, sandboxName, NewVolume.apply("new-gpu"), List.of(
                PythonGPUStackConfiguration.apply(sandboxName, List.of(), "gpusmall", "3.10", "john.doe@zurich.ch", Map.of())
            ))
            .run(user, runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(runtime));
    }

    public void $_creates_an_application_$_in_workspace_$(User executor, String applicationName,
                                                          String workspaceName) throws ExecutionException,
        InterruptedException {
        CreateApplicationCommand
            .apply(workspaceName, applicationName, "")
            .run(executor, runtime)
            .toCompletableFuture()
            .get().toPlainText(runtime);
    }

    public void $_removes_an_application_$_in_workspace_$(User executor, String applicationName,
                                                          String workspaceName) throws ExecutionException,
        InterruptedException {
        RemoveApplicationCommand
            .apply(workspaceName, applicationName)
            .run(executor, runtime)
            .toCompletableFuture()
            .get().toPlainText(runtime);
    }

    public void $_oauth_application_gets_self(User executor) throws ExecutionException, InterruptedException {
        OauthGetSelfCommand
            .apply()
            .run(executor, runtime)
            .toCompletableFuture()
            .get().toPlainText(runtime);
    }

    public void $_lists_applications_in_workspace_$(User executor, String workspaceName) throws ExecutionException,
        InterruptedException {
        results.add(
            ListApplicationsCommand
                .apply(workspaceName)
                .run(executor, runtime)
                .toCompletableFuture()
                .get()
                .toPlainText(runtime)
        );
    }

    public void $_imports_model_$_of_version_$_from_$_to_central_model_registry(
        AuthenticatedUser user, String modelName, String version, String workspaceName) throws ExecutionException,
        InterruptedException {
        var result = ImportToRegistryCommand
            .apply(workspaceName, modelName, version)
            .run(user, runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(runtime);

        mentionedWorkspace = workspaceName;
        results.add(result);
    }
}
