/*
 *
 * Project reducer
 *
 */
import _ from 'lodash';
import produce from 'immer';
import { GET_PROJECT, GET_PROJECT_FAILED, GET_PROJECT_SUCCESS } from './constants';

export const initialState = {
  id: false,
  loading: false,
  error: false,
  project: false
};

/* eslint-disable default-case, no-param-reassign */
const projectReducer = (state = initialState, action) =>
  produce(state, (draft) => {
    switch (action.type) {
      case GET_PROJECT:
        draft.id = action.name;
        draft.loading = true;
        draft.error = false;
        break;
      case GET_PROJECT_FAILED:
        draft.loading = false;
        draft.error = _.get(action, 'error.response.message') || 'Unknown error occurred creating the project';
        break;
      case GET_PROJECT_SUCCESS:
        draft.loading = false;
        draft.project = action.response.data;
        break;
    }
  });

export default projectReducer;
