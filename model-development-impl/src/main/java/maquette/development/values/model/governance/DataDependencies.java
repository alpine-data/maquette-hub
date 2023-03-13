package maquette.development.values.model.governance;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.UID;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Value class to store information about data assets which have been accessed during training process.
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DataDependencies {

    private static final String CHECKED = "checked";
    private static final String ASSETS = "assets";

    /**
     * Moment when dependency check was executed.
     */
    @JsonProperty(CHECKED)
    Instant checked;

    /**
     * UIDs of dependent data assets.
     */
    @JsonProperty(ASSETS)
    List<UID> assets;

    /**
     * Creates a new instance (from JSON).
     *
     * @param checked The moment when the dependencies have been checked.
     * @param assets The list of detected data assets (IDs of the assets as tracked in Data shop.
     * @return A new instance.
     */
    @JsonCreator
    public static DataDependencies apply(
        @JsonProperty(CHECKED) Instant checked,
        @JsonProperty(ASSETS) List<UID> assets) {

        if (Objects.isNull(assets)) {
            assets = List.of();
        }

        return new DataDependencies(checked, List.copyOf(assets));
    }

}
