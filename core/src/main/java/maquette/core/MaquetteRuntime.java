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

    List<MaquetteModule> modules;

    public static MaquetteRuntime apply() {
        var cfg = MaquetteConfiguration.apply();
        var omf = DefaultObjectMapperFactory.apply();
        var ath = DefaultAuthenticationHandler.apply();
        var usr = InMemoryUsersRepository.apply();

        return apply(null, cfg, omf, ath, usr, Lists.newArrayList(), Lists.newArrayList());
    }

    public MaquetteRuntime initialize() {
        /*
         * Initialize core modules.
         */
        this.withModule(UserModule.apply(this, this.getUsersRepository()));

        /*
         * Initialize modules
         */
        List<MaquetteModule> initializedModules = Lists.newArrayList();
        for (var moduleFactory : this.getModuleFactories()) {
            initializedModules.add(Operators.suppressExceptions(() -> moduleFactory.apply(this)));
        }

        this.setModules(initializedModules);

        for (var module : this.getModules()) {
            LOG.info("Starting module {}", module.getName());
            module.start(this);
        }

        return this;
    }

    private void setModules(List<MaquetteModule> initializedModules) {
        this.modules.clear();
        this.modules.addAll(initializedModules);
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
        this.moduleFactories.add(module);
        return this;
    }

    public MaquetteRuntime withModule(MaquetteModule module) {
        this.moduleFactories.add(mr -> module);
        return this;
    }

    public Javalin getApp() {
        if (Objects.isNull(app)) {
            throw new IllegalStateException("The runtime is not properly initialized yet.");
        }

        return app;
    }
}
