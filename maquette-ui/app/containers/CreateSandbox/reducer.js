/*
 *
 * CreateSandbox reducer
 *
 */
import _ from 'lodash';
import produce from 'immer';
import { 
  CREATE_SANDBOX, CREATE_SANDBOX_SUCCESS,
  INITIALIZE, FAILED, 
  GET_PROJECTS_SUCCESS, GET_STACKS_SUCCESS } from './constants';

export const initialState = {
  projects: false,
  stacks: false,
  loading: [],
  errors: {}
};

/* eslint-disable default-case, no-param-reassign */
const createSandboxReducer = (state = initialState, action) =>
  produce(state, draft => {
    switch (action.type) {
      case CREATE_SANDBOX:
        draft.errors = _.assign(draft.error, { 'create-sandbox': false });
        draft.loading = _.concat(draft.loading, ['create-sandbox']);
        break;

      case CREATE_SANDBOX_SUCCESS:
        draft.loading = _.without(draft.loading, 'create-sandbox');
        break;

      case INITIALIZE:
        draft.errors = _.assign(draft.errors, { stacks: false });
        draft.loading = _.concat(draft.loading, ['stacks']);
        draft.loading = _.concat(draft.loading, ['projects']);
        break;

      case FAILED:
        draft.loading = _.without(draft.loading, action.key);
        draft.errors = _.assign(draft.errors, { [action.key]: action.error });
        break;

      case GET_PROJECTS_SUCCESS:
        draft.loading = _.without(draft.loading, 'projects');
        draft.projects = action.response;
        break;

      case GET_STACKS_SUCCESS:
        draft.loading = _.without(draft.loading, 'stacks');
        draft.stacks = action.response.data;
        break;
    }
  });

export default createSandboxReducer;
