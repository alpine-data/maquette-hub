/*
 *
 * CreateStream reducer
 *
 */
import _ from 'lodash';
import produce from 'immer';
import { CREATE, CREATE_FAILED, CREATE_SUCCESS } from './constants';

export const initialState = {
  creating: false,
  error: false
};

/* eslint-disable default-case, no-param-reassign */
const createStreamReducer = (state = initialState, action) =>
  produce(state, (draft) => {
    switch (action.type) {
      case CREATE:
        draft.error = false;
        draft.creating = true;
        break;

      case CREATE_FAILED:
        draft.error = _.get(action, 'error.response.message', 'An error occurred creating the data source.');
        draft.creating = false;
        break;

      case CREATE_SUCCESS:
        draft.creating = false;
        break;
    }
  });

export default createStreamReducer;
