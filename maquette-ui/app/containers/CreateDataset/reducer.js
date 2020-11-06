/*
 *
 * CreateDataset reducer
 *
 */
import produce from 'immer';
import { 
  CREATE_DATASET, CREATE_DATASET_FAILURE, CREATE_DATASET_SUCCESS,
  LOAD_PROJECTS, LOAD_PROJECTS_FAILURE, LOAD_PROJECTS_SUCCESS } from './constants';

export const initialState = {
  creating: false,
  loading: false,
  error: false,
  user: false,
  projects: []
};

/* eslint-disable default-case, no-param-reassign */
const createDatasetReducer = (state = initialState, action) =>
  produce(state, (draft) => {
    switch (action.type) {
      case CREATE_DATASET:
        draft.creating = true;
        draft.error = false;
        break;
      case CREATE_DATASET_SUCCESS:
        draft.creating = false;
        draft.projects = [];
        draft.user = false;
        break;
      case CREATE_DATASET_FAILURE:
        draft.creating = false;
        draft.error = _.get(action, 'error.response.message') || 'Unknown error occurred creating the dataset';
        break;
      case LOAD_PROJECTS:
        draft.loading = true;
        draft.error = false;
        draft.projects = [];
        draft.user = action.user;
        break;
      case LOAD_PROJECTS_SUCCESS:
        draft.loading = false;
        draft.projects = _.map(action.response, p => { return { label: p.title, value: p.name, role: 'Master' } })
        break;
      case LOAD_PROJECTS_FAILURE:
        draft.loading = false;
        draft.error = _.get(action, 'error.response.message') || 'Unknown error occurred while fetching available projects';
      case "@@router/LOCATION_CHANGE":
        draft.user = false;
    }
  });

export default createDatasetReducer;
