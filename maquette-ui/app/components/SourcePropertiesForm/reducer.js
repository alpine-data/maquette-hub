import produce from 'immer';

import constants from './constants';

export default function(container) {
    const { TEST_CONNECTION, TEST_CONNECTION_SUCCESS } = constants(container);

    const initialState = {
        isTesting: false,
        testResult: {}
    };

    return (state = initialState, action) => 
        produce(state, (draft) => {
            switch (action.type) {
                case TEST_CONNECTION:
                    draft.isTesting = true;
                    draft.testResult = {};
                    break;
        
                case TEST_CONNECTION_SUCCESS:
                    draft.isTesting = false;
                    draft.testResult = _.get(action, 'response.data');
                    break;
            }            
        });
}