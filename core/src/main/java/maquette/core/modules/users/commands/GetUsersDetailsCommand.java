package maquette.core.modules.users.commands;

import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.modules.users.UserModule;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.TableResult;
import maquette.core.values.UID;
import maquette.core.values.user.User;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.util.List;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public final class GetUsersDetailsCommand implements Command {

    List<UID> users;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime
            .getModule(UserModule.class)
            .getServices()
            .getUsers(user, users)
            .thenApply(profiles -> {
                var table = Table
                    .create()
                    .addColumns(StringColumn.create("id"))
                    .addColumns(StringColumn.create("name"))
                    .addColumns(StringColumn.create("title"))
                    .addColumns(StringColumn.create("avatar"))
                    .addColumns(StringColumn.create("bio"))
                    .addColumns(StringColumn.create("location"))
                    .addColumns(StringColumn.create("email"))
                    .addColumns(StringColumn.create("phone"));

                profiles
                    .values()
                    .forEach(p -> {
                        var row = table.appendRow();
                        row.setString("id", p
                            .getId()
                            .getValue());
                        row.setString("name", p.getName());
                        row.setString("title", p.getTitle());
                        row.setString("avatar", p.getAvatar());
                        row.setString("bio", p.getBio());
                        row.setString("location", p.getLocation());
                        row.setString("email", p.getEmail());
                        row.setString("phone", p.getPhone());
                    });

                return TableResult.apply(table.sortDescendingOn("name"), profiles);
            });
    }

    @Override
    public Command example() {
        return GetUsersDetailsCommand.apply(Lists.newArrayList(UID.apply("alice"), UID.apply("bob")));
    }

}
