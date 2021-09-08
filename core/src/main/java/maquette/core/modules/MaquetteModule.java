package maquette.core.modules;

import com.google.common.collect.Maps;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;

import java.util.Map;

/**
 * Abstract interface for definition of Maquette Modules.
 */
public interface MaquetteModule {

    /**
     * Technical name of the module.
     *
     * @return The name, obviously.
     */
    String getName();

    /**
     * Will be called during start-up of Maquette.
     *
     * @param runtime The initialized Maquette runtime configuration.
     */
    default void start(MaquetteRuntime runtime) {
        // do nothing by default
    }

    /**
     * Will be called during shutdown of Maquette.
     */
    default void stop() {
        // do nothing by default
    }

    default Map<String, Class<? extends Command>> getCommands() {
        return Maps.newHashMap();
    }

}
