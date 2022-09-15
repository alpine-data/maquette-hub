package maquette.datashop.values.access;


import lombok.AllArgsConstructor;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.user.User;
import maquette.datashop.values.access_requests.DataAccessRequest;
import maquette.datashop.values.access_requests.DataAccessRequestState;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public class DataAssetMembers {

    /**
     * The list of existing (for the user accessible) access requests.
     */
    private final List<DataAccessRequest> accessRequests;

    /**
     * The list of members of the data asset.
     */
    private final List<GrantedAuthorization<DataAssetMemberRole>> members;

    /**
     * Returns a list of members with the given role.
     *
     * @param role The role the members should have.
     * @return The list of members.
     */
    public List<GrantedAuthorization<DataAssetMemberRole>> getMembers(DataAssetMemberRole role) {
        return this
            .members
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
            .accessRequests
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
        return this
            .members
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

