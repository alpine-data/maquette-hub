/*
 *
 * Dashboard reducer
 *
 */
import _ from 'lodash';
import produce from 'immer';
import { GET_PROJECTS, GET_PROJECTS_FAILED, GET_PROJECTS_SUCCESS } from './constants';

export const initialState = {
  loading: false,
  error: false,
  projects: false,
  user: false
};

/* eslint-disable default-case, no-param-reassign */
const dashboardReducer = (state = initialState, action) =>
  produce(state, (draft) => {
    switch (action.type) {
      case GET_PROJECTS:
        draft.loading = true;
        draft.error = false;
        draft.user = action.user;
        break;

      case GET_PROJECTS_FAILED:
        draft.loading = false;
        draft.error = _.get(action, 'error.response.message') || 'Unknown error occurred loading projects';
        break;

      case GET_PROJECTS_SUCCESS:
        draft.loading = false;
        draft.projects = action.response;
        break;

      case "@@router/LOCATION_CHANGE":
        draft.user = false;
        break;
    }
  });

export default dashboardReducer;
