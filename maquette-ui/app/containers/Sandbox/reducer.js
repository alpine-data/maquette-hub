/*
 *
 * Sandbox reducer
 *
 */
import _ from 'lodash';
import produce from 'immer';
import {
  INITIALIZE, FAILED,
  GET_SANDBOX_SUCCESS, GET_STACKS_SUCCESS, GET_PROJECT_SUCCESS } from './constants';

export const initialState = {
  project: false,
  stacks: false,
  sandbox: false,
  loading: [],
  errors: {}
};

/* eslint-disable default-case, no-param-reassign */
const sandboxReducer = (state = initialState, action) =>
  produce(state, (draft => {
    switch (action.type) {
      case INITIALIZE:
        draft.errors = _.assign(draft.errors, { project: false, sandbox: false, stacks: false });
        draft.loading = _.concat(draft.loading, ['project', 'stacks', 'sandbox']);
        break;

      case FAILED:
        draft.loading = _.without(draft.loading, action.key);
        draft.errors = _.assign(draft.errors, { [action.key]: action.error });
        break;

      case GET_PROJECT_SUCCESS:
        draft.loading = _.without(draft.loading, 'project');
        draft.project = action.response.data;
        break;

      case GET_SANDBOX_SUCCESS:
        draft.loading = _.without(draft.loading, 'sandbox');
        draft.sandbox = action.response.data;
        break;

      case GET_STACKS_SUCCESS:
        draft.loading = _.without(draft.loading, 'stacks');
        draft.stacks = action.response.data;
    }
  }));

export default sandboxReducer;
