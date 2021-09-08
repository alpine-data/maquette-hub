package maquette.datashop.values.access_requests;

import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

class DataAccessRequestCompanion {

    private DataAccessRequestCompanion() {

    }

    public static DataAccessRequestStatus getStatus(List<DataAccessRequestEvent> events) {
        var latest = events.get(0);

        if (latest instanceof Requested) {
            return DataAccessRequestStatus.REQUESTED;
        } else if (latest instanceof Granted) {
            return DataAccessRequestStatus.GRANTED;
        } else if (latest instanceof Rejected) {
            return DataAccessRequestStatus.REJECTED;
        } else if (latest instanceof Expired) {
            return DataAccessRequestStatus.EXPIRED;
        } else if (latest instanceof Withdrawn) {
            return DataAccessRequestStatus.WITHDRAWN;
        } else {
            throw new IllegalStateException("Unknown event " + latest);
        }
    }

    public static Set<DataAccessRequestAction> getActions(List<DataAccessRequestEvent> events, boolean canGrant,
                                                          boolean canRequest) {
        var result = Sets.<DataAccessRequestAction>newHashSet();
        var latest = events.get(0);

        if (latest instanceof Requested) {
            if (canGrant) {
                result.add(DataAccessRequestAction.RESPOND);
            } else if (canRequest) {
                result.add(DataAccessRequestAction.WITHDRAW);
            }
        }

        if (latest instanceof Granted) {
            result.add(DataAccessRequestAction.WITHDRAW);
        }

        if (latest instanceof Rejected || latest instanceof Withdrawn || latest instanceof Expired) {
            if (canRequest) {
                result.add(DataAccessRequestAction.REQUEST);
            }
        }

        return result;
    }

}
