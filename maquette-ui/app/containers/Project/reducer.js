/*
 *
 * Project reducer
 *
 */
import _ from 'lodash';
import produce from 'immer';
import { 
  INIT,
  FAILED,
  GET_DATA_ASSETS_SUCCESS,
  GET_PROJECT_SUCCESS,
  GET_STACKS_SUCCESS, 
  GET_SANDBOXES_SUCCESS } from './constants';

export const initialState = {
  'data-assets': [],
  sandboxes: [],
  stacks: [],
  project: false,

  loading: [],
  errors: {},
};

/* eslint-disable default-case, no-param-reassign */
const projectReducer = (state = initialState, action) =>
  produce(state, (draft) => {
    switch (action.type) {
      case INIT:
        draft.errors = initialState.errors;
        draft.loading = _.concat(draft.loading, ['data-assets', 'sandboxes', 'stacks', 'project'])
        break;

      case FAILED:
        draft.loading = _.without(draft.loading, action.key);
        draft.errors = _.assign(draft.errors, { [action.key]: action.error });
        break;

      case GET_PROJECT_SUCCESS:
        draft.loading = _.without(draft.loading, 'project');
        draft.project = action.response.data;
        break;

      case GET_DATA_ASSETS_SUCCESS:
        draft.loading = _.without(draft.loading, 'data-assets');
        draft[`data-assets`] = action.response;
        break;

      case GET_SANDBOXES_SUCCESS:
        draft.loading = _.without(draft.loading, 'sandboxes');
        draft.sandboxes = action.response.data;
        break;
        
      case GET_STACKS_SUCCESS:
        draft.loading = _.without(draft.loading, 'stacks');
        draft.stacks = action.response.data;
        break;
    }
  });

export default projectReducer;
