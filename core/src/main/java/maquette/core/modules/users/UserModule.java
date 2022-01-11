package maquette.core.modules.users;

import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.modules.MaquetteModule;
import maquette.core.modules.ports.AuthenticationTokenStore;
import maquette.core.modules.ports.InMemoryAuthenticationTokenStore;
import maquette.core.modules.ports.UsersRepository;
import maquette.core.modules.users.commands.*;
import maquette.core.modules.users.services.UserServices;
import maquette.core.modules.users.services.UserServicesFactory;
import maquette.core.server.commands.Command;
import maquette.core.values.user.User;

import java.util.Map;
import java.util.Objects;

@AllArgsConstructor(staticName = "apply", access = AccessLevel.PRIVATE)
public final class UserModule implements MaquetteModule {

    private final UserServices services;

    public static UserModule apply(MaquetteRuntime runtime, UsersRepository repository,
                                   AuthenticationTokenStore authenticationTokenStore) {
        var users = UserEntities.apply(repository, authenticationTokenStore, runtime.getObjectMapperFactory()
            .createJsonMapper());
        var services = UserServicesFactory.apply(users);
        return apply(services);
    }

    public static UserModule apply(MaquetteRuntime runtime, UsersRepository repository) {
        return apply(runtime, repository, InMemoryAuthenticationTokenStore.apply());
    }

    @Override
    public String getName() {
        return "users";
    }

    @Override
    public void start(MaquetteRuntime runtime) {
        MaquetteModule.super.start(runtime);

        runtime
            .getApp()
            .get("/api/users/login", ctx -> {
                var id = Objects.requireNonNull(ctx.queryParam("id"));
                var user = (User) Objects.requireNonNull(ctx.attribute("user"));

                var result = services
                    .registerAuthenticationToken(user, id)
                    .thenApply(d -> "Successfully logged in. You can close this window now.")
                    .toCompletableFuture();

                ctx.result(result);
            })
            .get("/api/users/token", ctx -> {
                var id = Objects.requireNonNull(ctx.queryParam("id"));

                var result = services
                    .getAuthenticationToken(id)
                    .toCompletableFuture();

                ctx.json(result);
            });
    }

    @Override
    public void stop() {
        MaquetteModule.super.stop();
    }

    @Override
    public Map<String, Class<? extends Command>> getCommands() {
        Map<String, Class<? extends Command>> commands = Maps.newHashMap();
        commands.put("users info", UserInformationCommand.class);
        commands.put("users get", GetUsersDetailsCommand.class); // TODO mw: Rename to users profiles
        commands.put("users profile", GetUserProfileCommand.class);
        commands.put("users update", UpdateUserCommand.class);
        commands.put("users list", QueryUsersCommand.class); // TODO mw: Rename to users query
        return commands;
    }

    public UserServices getServices() {
        return services;
    }

}
