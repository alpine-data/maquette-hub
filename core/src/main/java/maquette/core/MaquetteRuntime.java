package maquette.core;

import akka.actor.ActorSystem;
import akka.japi.Function;
import akka.japi.Procedure;
import com.google.common.collect.Lists;
import io.javalin.Javalin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
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

/**
 * MaquetteRuntime encapsulates all core elements of Maquette, to enabled modules to access these (e.g. the
 * underlying Actor System,
 * the Web Server, configuration, etc.),
 * <p>
 * The runtime has two states: Before initialization and after initialization. Before initialization new modules (or
 * module factories) can be registered.
 * Afterwards (after initialization) all values are only for read only.
 */
@Getter
@Setter
@AllArgsConstructor(staticName = "apply")
public final class MaquetteRuntime {

    private static final Logger LOG = LoggerFactory.getLogger(Maquette.class);

    /**
     * The underlying webserver. This will be set and initialized during initialization.
     */
    @With
    @Nullable
    Javalin app;

    /**
     * The underlying actor system. This will be set and initilaized during intialization.
     */
    @With
    @Nullable
    ActorSystem system;

    /**
     * The application configuration of this Maquette instance.
     */
    @With
    MaquetteConfiguration config;

    /**
     * The default object mapper factory of Maquette.
     */
    @With
    ObjectMapperFactory objectMapperFactory;

    /**
     * The authentication handler configured for the Maquette instance. It's responsible to identify (authenticate)
     * as user based on the HTTP request, received by Maquette's webserver.
     */
    @With
    AuthenticationHandler authenticationHandler;

    /**
     * An instance of a database port where user information can be stored.
     */
    @With
    UsersRepository usersRepository;

    /**
     * A list of modules which should be initialized during initialization. The list can only be modified before initialization.
     */
    List<Function<MaquetteRuntime, MaquetteModule>> moduleFactories;

    /**
     * The list of initialized modules. This list is only filled after initialization.
     */
    List<MaquetteModule> modules;

    /**
     * A list of listeners which are registered to be notified before initialization of modules start. The actor system
     * and the webserver will already be initialized, when these listeners are notified.
     */
    List<Procedure<MaquetteRuntime>> onBeforeInitializationListeners;

    /**
     * A list of listeners which get notified as soon as initialization of modules has been finished.
     */
    List<Procedure<MaquetteRuntime>> onAfterInitializationListeners;

    /**
     * Creates a new runtime instance with meaningful defaults.
     *
     * @return The initial MaquetteRuntime.
     */
    public static MaquetteRuntime apply() {
        var cfg = MaquetteConfiguration.apply();
        var omf = DefaultObjectMapperFactory.apply();
        var ath = DefaultAuthenticationHandler.apply();
        var usr = InMemoryUsersRepository.apply();

        return apply(null, null, cfg, omf, ath, usr, Lists.newArrayList(), Lists.newArrayList(), Lists.newArrayList()
            , Lists
            .newArrayList());
    }

    /**
     * Run initialization of the MaquetteRuntime, this method can only be called once.
     *
     * @return The initialized runtime.
     */
    public MaquetteRuntime initialize(ActorSystem system, Javalin app) {
        shouldNotBeInitialized();

        /*
         * Initialize core modules.
         */
        this.withModule(UserModule.apply(this, this.getUsersRepository()));

        /*
         * Set core components
         */
        this.setApp(app);
        this.setSystem(system);

        /*
         * Call before initialization listeners.
         */
        this.onBeforeInitializationListeners.forEach(
            p -> Operators.suppressExceptions(() -> p.apply(this)));

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

        /*
         * Call after initialization handlers.
         */
        this.onAfterInitializationListeners.forEach(p -> Operators.suppressExceptions(() -> p.apply(this)));

        return this;
    }

    /**
     * Get a specific module based on its type.
     *
     * @param type The type of the module.
     * @param <T> The type of the module.
     * @return The Maquette Module.
     */
    @SuppressWarnings("unchecked")
    public <T extends MaquetteModule> T getModule(Class<T> type) {
        shouldBeInitialized();

        return modules
            .stream()
            .filter(type::isInstance)
            .map(m -> (T) m)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(String.format("The module of type `%s` has not been " +
                "registered.", type
                .getName())));
    }

    /**
     * Register a listener which should be notified before module initialization. At this moment the Webserver and the actor system
     * have been already initialized and can be used.
     *
     * @param listener The listener to register.
     * @return This Maquette runtime.
     */
    public MaquetteRuntime onBeforeInitialization(Procedure<MaquetteRuntime> listener) {
        shouldNotBeInitialized();

        this.onBeforeInitializationListeners.add(listener);
        return this;
    }

    /**
     * Register a lister which should be notified as soon as initialization has been executed successfully.
     *
     * @param listener The listener to register.
     * @return This Maquette runtime.
     */
    public MaquetteRuntime onAfterInitialization(Procedure<MaquetteRuntime> listener) {
        shouldNotBeInitialized();

        this.onAfterInitializationListeners.add(listener);
        return this;
    }

    /**
     * Register a new module factory. This method should only be called before initialization.
     *
     * @param module The module factory to be registered.
     * @return This Maquette runtime.
     */
    public MaquetteRuntime withModule(Function<MaquetteRuntime, MaquetteModule> module) {
        shouldNotBeInitialized();

        this.moduleFactories.add(module);
        return this;
    }

    /**
     * Register a new module. This method should only be called before initialization.
     *
     * @param module The module to be registered.
     * @return This Maquette runtime.
     */
    public MaquetteRuntime withModule(MaquetteModule module) {
        shouldNotBeInitialized();

        this.moduleFactories.add(mr -> module);
        return this;
    }

    /**
     * Returns the webserver of the Maquette instance, the webserver will only be available after initialization.
     *
     * @return The webserver.
     */
    public Javalin getApp() {
        if (Objects.isNull(app)) {
            throw new IllegalStateException("The runtime is not properly initialized yet. The web server can only be used during or after the initialization.");
        }

        return app;
    }

    public ActorSystem getSystem() {
        if (Objects.isNull(system)) {
            throw new IllegalStateException("The runtime is not properly initialized yet. The actor system can only be used during or after initialization.");
        }

        return system;
    }

    private void shouldBeInitialized() {
        if (Objects.isNull(app)) {
            throw new IllegalStateException("Maquette Runtime is not initialized yet. This method can only be called after initialization.");
        }
    }

    private void shouldNotBeInitialized() {
        if (!Objects.isNull(app)) {
            throw new IllegalStateException("This method should be called before initialization of Maquette runtime.");
        }
    }

    private void setModules(List<MaquetteModule> initializedModules) {
        this.modules.clear();
        this.modules.addAll(initializedModules);
    }

}
