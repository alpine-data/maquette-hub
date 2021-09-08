package maquette.datashop.values.access;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * Helper class which helps to specify which actions are allowed based on user's roles
 * for a data asset.
 */
@Value
@AllArgsConstructor(staticName = "apply")
public class DataAssetPermissions {

    boolean owner;
    boolean steward;
    boolean consumer;
    boolean producer;
    boolean member;
    boolean subscriber;

    @JsonProperty
    public boolean canChangeSettings() {
        return owner || steward;
    }

    @JsonProperty
    public boolean canManageAccessRequests() {
        return owner || steward;
    }

    @JsonProperty
    public boolean canConsume() {
        return owner || steward || consumer || member || subscriber;
    }

    @JsonProperty
    public boolean canManageState() {
        return owner;
    }

    @JsonProperty
    public boolean canProduce() {
        return owner || producer || steward || member;
    }

    @JsonProperty
    public boolean canReview() {
        return owner;
    }

    @JsonProperty
    public boolean canReviewLogs() {
        return owner || steward;
    }

}
