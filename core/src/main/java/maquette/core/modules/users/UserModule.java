package maquette.core.modules.users;

import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.modules.MaquetteModule;
import maquette.core.modules.ports.UsersRepository;
import maquette.core.modules.users.commands.GetUsersDetailsCommand;
import maquette.core.modules.users.commands.UpdateUserCommand;
import maquette.core.modules.users.commands.UserInformationCommand;
import maquette.core.modules.users.services.UserServices;
import maquette.core.modules.users.services.UserServicesFactory;
import maquette.core.server.commands.Command;

import java.util.Map;

@AllArgsConstructor(staticName = "apply", access = AccessLevel.PRIVATE)
public final class UserModule implements MaquetteModule {

    private final UserServices services;

    public static UserModule apply(MaquetteRuntime runtime, UsersRepository repository) {
        var users = UserEntities.apply(repository, runtime.getObjectMapperFactory().createJsonMapper());
        var services = UserServicesFactory.apply(users);
        return apply(services);
    }

    @Override
    public String getName() {
        return "users";
    }

    @Override
    public void start(MaquetteRuntime runtime) {
        MaquetteModule.super.start(runtime);
    }

    @Override
    public void stop() {
        MaquetteModule.super.stop();
    }

    @Override
    public Map<String, Class<? extends Command>> getCommands() {
        Map<String, Class<? extends Command>> commands = Maps.newHashMap();
        commands.put("users info", UserInformationCommand.class);
        commands.put("users get", GetUsersDetailsCommand.class);
        commands.put("users update", UpdateUserCommand.class);
        return commands;
    }

    public UserServices getServices() {
        return services;
    }

}
