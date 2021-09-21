package maquette.core;

import akka.japi.Function;
import com.google.common.collect.Lists;
import io.javalin.Javalin;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.common.Operators;
import maquette.core.config.MaquetteConfiguration;
import maquette.core.databind.DefaultObjectMapperFactory;
import maquette.core.databind.ObjectMapperFactory;
import maquette.core.modules.MaquetteModule;
import maquette.core.modules.ports.InMemoryUsersRepository;
import maquette.core.modules.ports.UsersRepository;
import maquette.core.modules.users.UserModule;
import maquette.core.server.auth.AuthenticationHandler;
import maquette.core.server.auth.DefaultAuthenticationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Value
@AllArgsConstructor(staticName = "apply")
public class MaquetteRuntime {

    private static final Logger LOG = LoggerFactory.getLogger(Maquette.class);

    @With
    @Nullable
    Javalin app;

    @With
    MaquetteConfiguration config;

    @With
    ObjectMapperFactory objectMapperFactory;

    @With
    AuthenticationHandler authenticationHandler;

    @With
    UsersRepository usersRepository;

    List<Function<MaquetteRuntime, MaquetteModule>> moduleFactories;

    @With
    List<MaquetteModule> modules;

    public static MaquetteRuntime apply() {
        var cfg = MaquetteConfiguration.apply();
        var omf = DefaultObjectMapperFactory.apply();
        var ath = DefaultAuthenticationHandler.apply();
        var usr = InMemoryUsersRepository.apply();

        return apply(null, cfg, omf, ath, usr, List.of(), List.of());
    }

    public MaquetteRuntime initialize() {
        /*
         * Initialize core modules.
         */
        var runtime = this;
        runtime = this.withModule(UserModule.apply(runtime, runtime.getUsersRepository()));

        /*
         * Initialize modules
         */
        List<MaquetteModule> initializedModules = Lists.newArrayList();
        for (var moduleFactory : runtime.getModuleFactories()) {
            var rt = runtime;
            initializedModules.add(Operators.suppressExceptions(() -> moduleFactory.apply(rt)));
        }

        runtime = runtime.withModules(initializedModules);

        for (var module : runtime.getModules()) {
            LOG.info("Starting module {}", module.getName());
            module.start(runtime);
        }

        return runtime;
    }

    @SuppressWarnings("unchecked")
    public <T extends MaquetteModule> T getModule(Class<T> type) {
        if (modules.size() == 0) {
            LOG.warn("Accessing getModule w/o registered modules. Are you calling the method before runtime is " +
                "completely initialized?");
        }

        return modules
            .stream()
            .filter(type::isInstance)
            .map(m -> (T) m)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(String.format("The module of type `%s` has not been " +
                "registered.", type
                .getName())));
    }

    public MaquetteRuntime withModule(Function<MaquetteRuntime, MaquetteModule> module) {
        var modulesNext = Lists.newArrayList(moduleFactories);
        modulesNext.add(module);

        return apply(app, config, objectMapperFactory, authenticationHandler, usersRepository,
            List.copyOf(modulesNext), modules);
    }

    public MaquetteRuntime withModule(MaquetteModule module) {
        var modulesNext = Lists.newArrayList(moduleFactories);
        modulesNext.add(mr -> module);
        return apply(app, config, objectMapperFactory, authenticationHandler, usersRepository,
            List.copyOf(modulesNext), modules);
    }

    public Javalin getApp() {
        if (Objects.isNull(app)) {
            throw new IllegalStateException("The runtime is not properly initialized yet.");
        }

        return app;
    }
}
