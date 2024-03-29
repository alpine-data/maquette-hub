package maquette.datashop.providers;

import akka.Done;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.core.values.user.User;
import maquette.datashop.entities.DataAssetEntity;
import maquette.datashop.values.DataAssetProperties;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * A data asset provider is a concrete implementation of a data asset (e.g. connecting to a database, or some file
 * storage).
 */
public interface DataAssetProvider {

    /**
     * Will be called when Maquette is initialized.
     *
     * @param runtime The Maquette runtime environment.
     */
    default void configure(MaquetteRuntime runtime) {

    }

    /**
     * This method should return default properties assigned to new data assets created with this provider.
     *
     * @return The default properties.
     */
    default Object getDefaultProperties() {
        return new Object();
    }

    /**
     * The method should return default settings assigned to the new data asset if no settings are provided.
     *
     * @return The default settings.
     */
    default DataAssetSettings getDefaultSettings() {
        return NoDataAssetSettings.apply();
    }

    /**
     * Should return the type of the settings class.
     *
     * @return The settings type of this provider.
     */
    default Class<? extends DataAssetSettings> getSettingsType() {
        return NoDataAssetSettings.class;
    }

    /**
     * Should return the properties type.
     *
     * @return The properties type of this provider.
     */
    default Class<?> getPropertiesType() {
        return Object.class;
    }

    /**
     * Specifies the type name of this provider. The name must be unique between all providers configured for a
     * single Maquette instance.
     *
     * @return The type name.
     */
    String getType();

    /**
     * Returns the type name in a pluralized forms (used for generating URLs, etc.)
     *
     * @return The pluralized type name.
     */
    default String getTypePluralized() {
        return getType() + "s";
    }

    /**
     * This callback is called by Maquette Data Shop before a new data asset of this is about to be created.
     *
     * @param executor       The user who created the asset.
     * @param properties     Data asset properties
     * @param customSettings The custom settings submitted during creation.
     * @return Done.
     */
    default CompletionStage<Done> beforeCreated(User executor, DataAssetProperties properties, Object customSettings) {
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    /**
     * This callback is called by Maquette Data Shop when a new data asset of this type has been created.
     *
     * @param executor       The user who created the asset.
     * @param entity         The newly created data asset.
     * @param customSettings The custom settings submitted during creation.
     * @return Done.
     */
    default CompletionStage<Done> onCreated(User executor, DataAssetEntity entity, Object customSettings) {
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    /**
     * This callback is called by Maquette Data Shop when custom settings have been updated.
     *
     * @param executor       The user who updated the asset.
     * @param entity         The updated entity.
     * @param customSettings The newly created custom settings.
     * @return Done.
     */
    default CompletionStage<Done> onUpdatedCustomSettings(User executor, DataAssetEntity entity,
                                                          Object customSettings) {
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    /**
     * May return additional details which are attached to the data asset properties.
     *
     * @param properties     The properties of the data asset.
     * @param customSettings The custom settings of the data asset.
     * @return Provider specific details of the data asset.
     */
    default CompletionStage<?> getDetails(DataAssetProperties properties, Object customSettings) {
        return CompletableFuture.completedFuture(new Object());
    }

    /**
     * Returns a set of provider-specific custom commands. The key is the command, the value is the implementation of
     * the command.
     *
     * @return The commands.
     */
    default Map<String, Class<? extends Command>> getCustomCommands() {
        return Map.of();
    }

}
