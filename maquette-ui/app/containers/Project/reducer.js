/*
 *
 * Project reducer
 *
 */
import _ from 'lodash';
import produce from 'immer';
import { 
  GET_DATASETS_FAILED, GET_DATASETS_SUCCESS,
  GET_PROJECT, GET_PROJECT_FAILED, GET_PROJECT_SUCCESS,
  GET_STACKS_SUCCESS, GET_STACKS_FAILED, GET_SANDBOXES_SUCCESS, GET_SANDBOXES_FAILED } from './constants';

export const initialState = {
  id: false,
  datasets: [],
  sandboxes: [],
  stacks: [],
  project_loading: false,
  dataset_loading: false,
  sandboxes_loading: false,
  stacks_loading: false,
  loading: false,
  error: false,
  project: false
};

/* eslint-disable default-case, no-param-reassign */
const projectReducer = (state = initialState, action) =>
  produce(state, (draft) => {
    switch (action.type) {
      case GET_PROJECT:
        if (action.clear) {
          draft.id = action.name;
          draft.project_loading = true;
          draft.dataset_loading = true;
          draft.sandboxes_loading = true;
          draft.stacks_loading = true;
          
          draft.sandboxes = [];
          draft.stacks = [];
        }
        draft.error = false;
        break;
      case GET_PROJECT_FAILED:
        draft.project_loading = false;
        draft.error = _.get(action, 'error.response.message') || 'Unknown error occurred creating the project';
        break;
      case GET_PROJECT_SUCCESS:
        draft.project_loading = false;
        draft.project = action.response.data;
        break;
      case GET_DATASETS_FAILED:
        draft.dataset_loading = false;
        draft.error = _.get(action, 'error.response.message') || 'Unkown error occurred fetching datasets';
        break;
      case GET_DATASETS_SUCCESS:
        draft.dataset_loading = false;
        draft.datasets = action.response;
        break;
      case GET_SANDBOXES_FAILED:
        draft.sandboxes_loading = false;
        draft.error = _.get(action, 'error.response.message') || 'Unkown error occurred fetching datasets';
        break;
      case GET_SANDBOXES_SUCCESS:
        draft.sandboxes_loading = false;
        draft.sandboxes = action.response.data;
        break;
      case GET_STACKS_FAILED:
        draft.stacks_loading = false;
        draft.error = _.get(action, 'error.response.message') || 'Unkown error occurred fetching datasets';
        break;
      case GET_STACKS_SUCCESS:
        draft.stacks_loading = false;
        draft.stacks = action.response.data;
        break;
    }

    draft.loading = draft.project_loading || draft.dataset_loading;
  });

export default projectReducer;
