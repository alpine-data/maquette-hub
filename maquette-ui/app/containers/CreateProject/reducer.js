/*
 *
 * CreateProject reducer
 *
 */
import _ from 'lodash';
import produce from 'immer';
import { CREATE_PROJECT, CREATE_PROJECT_FAILED, CREATE_PROJECT_SUCCESS } from './constants';

export const initialState = {
  loading: false,
  error: false
};

/* eslint-disable default-case, no-param-reassign */
const createProjectReducer = (state = initialState, action) =>
  produce(state, (draft) => {
    switch (action.type) {
      case CREATE_PROJECT:
        draft.loading = true;
        draft.error = false;
        window.scrollTo(0, 0);
        break;
      case CREATE_PROJECT_FAILED:
        draft.loading = false;
        draft.error = _.get(action, 'error.response.message') || 'Unknown error occurred creating the project';
        break;
      case CREATE_PROJECT_SUCCESS:
        draft.loading = false;
        draft.error = false;
        break;
    }
  });

export default createProjectReducer;
