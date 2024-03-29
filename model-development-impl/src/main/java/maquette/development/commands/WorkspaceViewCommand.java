package maquette.development.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.common.Operators;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.values.authorization.ApplicationAuthorization;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;
import maquette.development.MaquetteModelDevelopment;
import maquette.development.commands.views.WorkspaceView;

import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class WorkspaceViewCommand implements Command {

    String name;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        var services = runtime.getModule(MaquetteModelDevelopment.class);

        var workspaceCS = services
            .getWorkspaceServices()
            .get(user, name)
            .thenApply(workspace ->
                // exclude applications from members
                workspace.withMembers(
                    workspace
                        .getMembers()
                        .stream()
                        .filter(member -> !(member.getAuthorization() instanceof ApplicationAuthorization))
                        .collect(Collectors.toList())
                ));

        var stacksCS = services
            .getSandboxServices()
            .getStacks(user);

        var sandboxOwnedCountCS = workspaceCS.thenApply(wks -> wks
            .getSandboxes()
            .stream()
            .filter(sdbx -> (user instanceof AuthenticatedUser) && sdbx
                .getProperties()
                .getCreated()
                .getBy()
                .equals(((AuthenticatedUser) user)
                    .getId()
                    .getValue()))
            .count());

        var workspacePermissionsCS = workspaceCS.thenApply(wks -> wks.getWorkspacePermissions(user));

        var workspaceApplicationsCS = services
            .getWorkspaceServices()
            .findApplicationsInWorkspace(runtime, user, name);

        return Operators.compose(
            workspaceCS, stacksCS, sandboxOwnedCountCS, workspacePermissionsCS, workspaceApplicationsCS,
            WorkspaceView::apply);
    }

    @Override
    public Command example() {
        return WorkspaceViewCommand.apply("some-workspace");
    }

}
