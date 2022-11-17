package maquette.development.commands.sandboxes;

import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.MessageResult;
import maquette.core.values.user.User;
import maquette.development.MaquetteModelDevelopment;
import maquette.development.values.sandboxes.volumes.NewVolume;
import maquette.development.values.sandboxes.volumes.VolumeDefinition;
import maquette.development.values.stacks.StackConfiguration;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CreateSandboxCommand implements Command {

    String workspace;

    String name;

    String comment;

    VolumeDefinition volume;

    List<StackConfiguration> stacks;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime
            .getModule(MaquetteModelDevelopment.class)
            .getSandboxServices()
            .createSandbox(user, workspace, name, comment, Optional.ofNullable(volume), stacks)
            .thenApply(done -> MessageResult.apply("Successfully created workspace"));
    }

    @Override
    public Command example() {
        return apply("some-workspace", "some-sandbox", "some comment", NewVolume.apply("some-volume"),
            Lists.newArrayList());
    }

}
