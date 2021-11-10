package maquette.datashop.values.access;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.user.User;

import java.util.List;

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

    public static DataAssetPermissions forUser(User executor, List<GrantedAuthorization<DataAssetMemberRole>> members, boolean isSubscriber) {
        var isOwner = members.stream().anyMatch(m -> m.getRole().equals(DataAssetMemberRole.OWNER) && m.getAuthorization().authorizes(executor));
        var isSteward = members.stream().anyMatch(m -> m.getRole().equals(DataAssetMemberRole.STEWARD) && m.getAuthorization().authorizes(executor));
        var isConsumer = members.stream().anyMatch(m -> m.getRole().equals(DataAssetMemberRole.CONSUMER) && m.getAuthorization().authorizes(executor));
        var isProducer = members.stream().anyMatch(m -> m.getRole().equals(DataAssetMemberRole.PRODUCER) && m.getAuthorization().authorizes(executor));
        var isMember = members.stream().anyMatch(m -> m.getRole().equals(DataAssetMemberRole.MEMBER) && m.getAuthorization().authorizes(executor));

        return apply(isOwner, isSteward, isConsumer, isProducer, isMember, isSubscriber);
    }

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
