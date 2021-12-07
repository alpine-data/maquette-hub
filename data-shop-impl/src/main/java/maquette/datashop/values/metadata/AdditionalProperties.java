package maquette.datashop.values.metadata;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AdditionalProperties {

    private static final String TIMELINESS = "timeliness";
    private static final String GEOGRAPHY = "geography";
    private static final String BU = "bu";
    private static final String LOB = "lob";

    @With
    @JsonProperty(TIMELINESS)
    Timeliness timeliness;

    @With
    @JsonProperty(GEOGRAPHY)
    String geography;

    @With
    @JsonProperty(BU)
    String bu;

    @With
    @JsonProperty(LOB)
    String lob;

    @JsonCreator
    public static AdditionalProperties apply(
            @JsonProperty(TIMELINESS) Timeliness timeliness,
            @JsonProperty(GEOGRAPHY) String geography,
            @JsonProperty(BU) String bu,
            @JsonProperty(LOB) String lob) {

        return new AdditionalProperties(timeliness, geography, bu, lob);
    }

    public static AdditionalProperties fake() {
        return AdditionalProperties.apply(Timeliness.DAILY, "EMEA", "ZCH", "bu");
    }

}
