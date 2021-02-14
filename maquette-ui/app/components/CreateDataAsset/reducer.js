import produce from 'immer';

import constants from './constants';

export default function(container, assetType = 'data asset') {
    const { CREATE, CREATE_SUCCESS, CREATE_FAILURE, FETCH, FETCH_SUCCESS, FETCH_FAILURE } = constants(container);

    const initialState = {
        error: false,
        data: false,
        loading: false
    };

    return (state = initialState, action) => 
        produce(state, (draft) => {
            switch (action.type) {
                case FETCH:
                    draft.loading = true;
                    draft.error = false;
                    draft.data = false;
                    break;

                case FETCH_SUCCESS:
                    draft.loading = false;
                    draft.data = action.response;
                    break;

                case CREATE:
                    draft.creating = true;
                    draft.error = false;
                    break;

                case CREATE_SUCCESS:
                    draft.loading = false;
                    break;

                case FETCH_FAILURE:
                case CREATE_FAILURE:
                    draft.loading = false;
                    draft.error = _.get(action, 'error.response.message') || `An error occurred creating the ${assetType}`;
                    break;
            }            
        });
}