package maquette.datashop.values.metadata;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.common.Operators;

import java.util.Locale;

@With
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DataAssetMetadata {

    private static final String TITLE = "title";
    private static final String NAME = "name";
    private static final String SUMMARY = "summary";
    private static final String VISIBILITY = "visibility";
    private static final String CLASSIFICATION = "classification";
    private static final String PERSONAL_INFORMATION = "personal-information";
    private static final String ZONE = "zone";

    /**
     * A speaking title for the data asset.
     */
    @JsonProperty(TITLE)
    String title;

    /**
     * A technical name for the data asset.
     */
    @JsonProperty(NAME)
    String name;

    /**
     * A short description of the data asset.
     */
    @JsonProperty(SUMMARY)
    String summary;

    /**
     * The visibility of the asset.
     */
    @JsonProperty(VISIBILITY)
    DataVisibility visibility;

    /**
     * Classification if the asset.
     */
    @JsonProperty(CLASSIFICATION)
    DataClassification classification;

    /**
     * Indicator whether personal information is included in the data.
     */
    @JsonProperty(PERSONAL_INFORMATION)
    PersonalInformation personalInformation;

    /**
     * The zone gives an indication how well prepared/ clean the data is.
     */
    @JsonProperty(ZONE)
    DataZone zone;

    @JsonCreator
    public static DataAssetMetadata apply(
        @JsonProperty(TITLE) String title,
        @JsonProperty(NAME) String name,
        @JsonProperty(SUMMARY) String summary,
        @JsonProperty(VISIBILITY) DataVisibility visibility,
        @JsonProperty(CLASSIFICATION) DataClassification classification,
        @JsonProperty(PERSONAL_INFORMATION) PersonalInformation personalInformation,
        @JsonProperty(ZONE) DataZone zone,
        @JsonProperty("personalInformation") PersonalInformation ignore) {

        return new DataAssetMetadata(title, name, summary, visibility, classification, personalInformation, zone);
    }

    public static DataAssetMetadata apply(
        @JsonProperty(TITLE) String title,
        @JsonProperty(NAME) String name,
        @JsonProperty(SUMMARY) String summary,
        @JsonProperty(VISIBILITY) DataVisibility visibility,
        @JsonProperty(CLASSIFICATION) DataClassification classification,
        @JsonProperty(PERSONAL_INFORMATION) PersonalInformation personalInformation,
        @JsonProperty(ZONE) DataZone zone) {
        return apply(title, name, summary, visibility, classification, personalInformation, zone, personalInformation);
    }

    /**
     * Creates a sample value object with default values.
     *
     * @param title The title for the asset.
     * @param name  The name of the asset.
     * @return A new instance.
     */
    public static DataAssetMetadata sample(String title, String name) {
        return apply(title, name, Operators.lorem(), DataVisibility.PUBLIC, DataClassification.PUBLIC,
            PersonalInformation.NONE, DataZone.RAW, PersonalInformation.PERSONAL_INFORMATION);
    }

    /**
     * Creates a sample value object with default values.
     *
     * @param title The title for the asset.
     * @return A new instance.
     */
    public static DataAssetMetadata sample(String title) {
        return sample(title, title.toLowerCase(Locale.ROOT).replace(' ', '-'));
    }

    public static DataAssetMetadata sample() {
        return sample("Some Asset");
    }

    @JsonProperty("personalInformation")
    public PersonalInformation getPI() {
        return personalInformation;
    }

    public void setPersonalInformation(PersonalInformation pi) {
        // do nothing
    }

}
