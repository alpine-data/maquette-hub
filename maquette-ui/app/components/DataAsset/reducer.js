import produce from 'immer';

import constants from './constants';

export default function(container) {
    const { FETCH, FETCH_SUCCESS, FETCH_FAILURE, UPDATE, UPDATE_FAILURE, UPDATE_SUCCESS, DISMISS_ERROR } = constants(container);

    const initialState = {
        fetchAction: false,
        view: false,

        error: false,
        loading: false,
        updating: false
    };

    return (state = initialState, action) => 
        produce(state, (draft) => {
            switch (action.type) {
                case FETCH:
                    draft.fetchAction = action;
                    draft.error = false;

                    if (action.clear || !draft.view) {
                        draft.loading = true;
                        draft.view = false;
                    } else {
                        draft.updating = true;
                    }

                    break;

                case FETCH_SUCCESS:
                    draft.loading = false;
                    draft.updating = false;
                    draft.view = action.response;
                    break;

                case UPDATE:
                    draft.updating = true;
                    draft.error = false;
                    break;

                case UPDATE_SUCCESS:
                    draft.updating = false;
                    break;

                case FETCH_FAILURE:
                case UPDATE_FAILURE:
                    draft.loading = false;
                    draft.updating = false;
                    draft.error = _.get(action, 'error.response.message') || `An error occurred processing the request.`;
                    break;

                case DISMISS_ERROR:
                    draft.error = false;
            }            
        });
}