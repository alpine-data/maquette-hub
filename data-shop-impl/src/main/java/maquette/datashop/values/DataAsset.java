package maquette.datashop.values;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.user.User;
import maquette.datashop.values.access.DataAssetMemberRole;
import maquette.datashop.values.access.DataAssetPermissions;
import maquette.datashop.values.access_requests.DataAccessRequest;
import maquette.datashop.values.access_requests.DataAccessRequestState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@With
@Getter
@AllArgsConstructor(staticName = "apply")
public final class DataAsset {

    private final DataAssetProperties properties;

    private final List<DataAccessRequest> accessRequests;

    private final List<GrantedAuthorization<DataAssetMemberRole>> members;

    @Nullable
    private final Object customSettings;

    @Nullable
    private final Object customProperties;

    @Nullable
    private final Object customDetails;

    /**
     * Returns a list of members with the given role.
     *
     * @param role The role the members should have.
     * @return The list of members.
     */
    public List<GrantedAuthorization<DataAssetMemberRole>> getMembers(DataAssetMemberRole role) {
        return getMembers()
            .stream()
            .filter(m -> m
                .getRole()
                .equals(role))
            .collect(Collectors.toList());
    }

    /**
     * Return a list of possible actions a user can do with the data asset.
     *
     * @param user The user for which the rights should be calculated.
     * @return The collected rights;
     */
    public DataAssetPermissions getDataAssetPermissions(User user) {
        var isOwner = this.isMember(user, DataAssetMemberRole.OWNER);
        var isSteward = this.isMember(user, DataAssetMemberRole.STEWARD);
        var isConsumer = this.isMember(user, DataAssetMemberRole.CONSUMER);
        var isProducer = this.isMember(user, DataAssetMemberRole.PRODUCER);
        var isMember = this.isMember(user, DataAssetMemberRole.MEMBER);

        var isSubscriber = this
            .getAccessRequests()
            .stream()
            .anyMatch(r -> r
                .getState()
                .equals(DataAccessRequestState.GRANTED));

        return DataAssetPermissions.apply(isOwner, isSteward, isConsumer, isProducer, isMember, isSubscriber);
    }

    /**
     * Check whether a user has a member role of the data asset.
     *
     * @param user The user to check.
     * @param role The role the user should have.
     * @return True if the user is a member with the given role.
     */
    public boolean isMember(User user, DataAssetMemberRole role) {
        return getMembers()
            .stream()
            .anyMatch(granted -> granted
                .getAuthorization()
                .authorizes(user) && (Objects.isNull(role) || granted
                .getRole()
                .equals(role)));
    }

    /**
     * Check whether a user is a member with any role of the data asset.
     *
     * @param user The user to check.
     * @return True if the user a member of the data asset.
     */
    public boolean isMember(User user) {
        return isMember(user, null);
    }

}
