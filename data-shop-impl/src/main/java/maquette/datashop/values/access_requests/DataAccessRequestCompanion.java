package maquette.datashop.values.access_requests;

import com.google.common.collect.Sets;
import maquette.datashop.values.access_requests.events.*;

import java.util.List;
import java.util.Set;

class DataAccessRequestCompanion {

    private DataAccessRequestCompanion() {

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
