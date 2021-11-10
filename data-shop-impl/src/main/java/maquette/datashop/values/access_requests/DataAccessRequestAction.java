package maquette.datashop.values.access_requests;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DataAccessRequestAction {

    RESPOND("respond", false, true, false),
    REQUEST("request", true, false, false),
    WITHDRAW("withdraw", true, true, true),
    REVIEW("review", false, false, true);

    @JsonValue
    private final String value;

    private final boolean canRequest;

    private final boolean canGrant;

    private final boolean canReview;

    DataAccessRequestAction(String value, boolean canRequest, boolean canGrant, boolean canReview) {
        this.value = value;
        this.canGrant = canGrant;
        this.canRequest = canRequest;
        this.canReview = canReview;
    }

    public String getValue() {
        return value;
    }

    /**
     * This method tells the caller whether a action can be executed by a user who has rights to grant access
     * to a data asset.
     *
     * @return yes/ no
     */
    public boolean isCanGrant() {
        return canGrant;
    }

    /**
     * This method tells the caller whether a action can be executed by a user who has rights to edit a data access request.
     *
     * @return yes/ no.
     */
    public boolean isCanRequest() {
        return canRequest;
    }

    /**
     * This method tells teh caller whether the action can be executed by a user who has the rights to review a
     * data asset.
     *
     * @return yes/ no.
     */
    public boolean isCanReview() { return canReview; }

}
