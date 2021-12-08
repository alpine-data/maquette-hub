package maquette.development.specs.steps;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;
import maquette.development.MaquetteModelDevelopment;
import maquette.development.commands.*;
import maquette.development.commands.members.GrantWorkspaceMemberCommand;
import maquette.development.ports.DataAssetsServicePort;
import maquette.development.values.EnvironmentType;
import maquette.development.values.WorkspaceMemberRole;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

@AllArgsConstructor()
public class WorkspaceStepDefinitions {

    protected final MaquetteRuntime runtime;

    protected final List<String> results;

    public WorkspaceStepDefinitions(MaquetteRuntime runtime) {
        this(runtime, Lists.newArrayList());
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

        results.add(result);
    }

    public void $_updates_a_workspace_with_name_$_to_a_new_name_$(AuthenticatedUser user,
                                                                  String workspaceName,
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
    }

    public void $_grants_$_access_to_the_$_workspace_for_$(User executor,
                                                           WorkspaceMemberRole memberRole,
                                                           String workspaceName,
                                                           AuthenticatedUser grantedUser) throws ExecutionException,
        InterruptedException {
        var result = GrantWorkspaceMemberCommand
            .apply(workspaceName, grantedUser.toAuthorization().toGenericAuthorizationDefinition(), memberRole)
            .run(executor, runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(runtime);

        results.add(result);
    }

    public void $_revoke_access_to_the_$_workspace_for_$(User executor,
                                                         String workspaceName,
                                                         AuthenticatedUser grantedUser) throws ExecutionException,
        InterruptedException {
        var result = RevokeWorkspaceMemberRoleCommand
            .apply(workspaceName, grantedUser.toAuthorization())
            .run(executor, runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(runtime);

        results.add(result);
    }

    public void $_gets_environment_for_workspace_$_of_type_$(AuthenticatedUser user,
                                                             String workspaceName,
                                                             EnvironmentType environmentType) throws ExecutionException, InterruptedException {
        var result = GetWorkspaceEnvironmentCommand
            .apply(workspaceName, environmentType)
            .run(user, runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(runtime);

        results.add(result);
    }

    public void $_configures_workspace_$_to_be_$(AuthenticatedUser user,
                                                 String workspaceName,
                                                 String newWorkspaceName,
                                                 String title,
                                                 String summary) throws ExecutionException, InterruptedException {
        var result = UpdateWorkspaceCommand
            .apply(
                workspaceName,
                newWorkspaceName,
                title,
                summary)
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
}
