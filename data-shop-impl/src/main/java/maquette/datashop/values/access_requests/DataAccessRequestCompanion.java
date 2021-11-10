package maquette.datashop.values.access_requests;

import com.google.common.collect.Sets;

import java.util.Set;

class DataAccessRequestCompanion {

    private DataAccessRequestCompanion() {

    }

    public static Set<DataAccessRequestAction> getActions(DataAccessRequestState state, boolean canGrant,
                                                          boolean canRequest, boolean canReview) {

        var result = Sets.<DataAccessRequestAction>newHashSet();

        switch (state) {
            case REQUESTED:
                if (canGrant) result.add(DataAccessRequestAction.RESPOND);
                if (canRequest) result.add(DataAccessRequestAction.WITHDRAW);
                break;
            case REJECTED:
                if (canRequest) {
                    result.add(DataAccessRequestAction.WITHDRAW);
                    result.add(DataAccessRequestAction.REQUEST);
                }
                break;
            case GRANTED:
                result.add(DataAccessRequestAction.WITHDRAW);
                break;
            case REVIEW_REQUIRED:
                if (canReview) result.add(DataAccessRequestAction.REVIEW);
                if (canRequest) result.add(DataAccessRequestAction.WITHDRAW);
                break;
            case WITHDRAWN:
                if (canRequest) result.add(DataAccessRequestAction.WITHDRAW);
                break;
            case EXPIRED:
                if (canRequest) {
                    result.add(DataAccessRequestAction.REQUEST);
                }
                break;
        }

        return result;
    }

}
