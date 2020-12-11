/*
 *
 * CreateCollection reducer
 *
 */

import produce from 'immer';
import { 
  CREATE, CREATE_FAILURE, CREATE_SUCCESS } from './constants';

export const initialState = {
  creating: false,
  error: false
};

/* eslint-disable default-case, no-param-reassign */
const createCollectionReducer = (state = initialState, action) =>
  produce(state, (draft) => {
    switch (action.type) {
      case CREATE:
        draft.creating = true;
        draft.error = false;
        break;
      case CREATE_SUCCESS:
        draft.creating = false;
        break;
      case CREATE_FAILURE:
        draft.creating = false;
        draft.error = _.get(action, 'error.response.message') || 'An error occurred creating the dataset';
        break;
    }
  });

export default createCollectionReducer;
