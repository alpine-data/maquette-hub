package maquette.datashop.providers;

public interface DataAssetSettings {

    /**
     * This method should return a new instance of the settings class, but may obfuscate certain secret properties.
     *
     * @return A new instance of the same object.
     */
    default DataAssetSettings getObfuscated() {
        return this;
    }

}
