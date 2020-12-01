/*
 *
 * CreateDataAccessRequest reducer
 *
 */
import produce from 'immer';
import { 
  INIT, FAILED, FETCHED,
  SUBMIT, SUBMIT_FAILED, SUBMIT_SUCCESS } from './constants';

export const initialState = {
  data: false,
  error: false,
  loading: false,

  submitting: false,
  submitError: false
};

/* eslint-disable default-case, no-param-reassign */
const createDataAccessRequestReducer = (state = initialState, action) =>
  produce(state, (draft) => {
    switch (action.type) {
      case INIT:
        draft.data = false;
        draft.loading = true;
        break;

      case FAILED:
        draft.loading = false;
        draft.error = _.get(action, 'error.response.message') || '¯\_(ツ)_/¯ unknown error occurred, sorry.';
        break;

      case FETCHED:
        draft.loading = false;
        draft.data = action.response;
        break;

      case SUBMIT:
        draft.submitting = true;
        draft.submitError = false;
        break;

      case SUBMIT_FAILED:
        draft.submitting = false;
        draft.submitError = _.get(action, 'error.response.message') || '¯\_(ツ)_/¯ unknown error occurred, sorry.';
        break;

      case SUBMIT_SUCCESS:
        draft.submitting = false;
        break;
    }
  });

export default createDataAccessRequestReducer;
