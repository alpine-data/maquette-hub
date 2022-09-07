package maquette.datashop.providers;

public interface DataAssetSettings {

    /**
     * This method should return a new instance of the settings class, but may obfuscate certain secret properties.
     * @return
     */
    default DataAssetSettings getObfuscated() {
        return this;
    }

}
