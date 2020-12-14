/*
 *
 * DataSource reducer
 *
 */
import produce from 'immer';
import { LOAD, UPDATE, FETCHED, FAILED, TEST_CONNECTION, TEST_CONNECTION_SUCCESS } from './constants';

export const initialState = {
  keys: {},
  data: false,

  error: false,
  loading: false,
  updating: false,

  testing: false,
  testResult: false
};


/* eslint-disable default-case, no-param-reassign */
const dataSourceReducer = (state = initialState, action) =>
  produce(state, (draft) => {
    switch (action.type) {
      case LOAD:
        draft.keys = action;
        draft.error = false;

        if (action.dataset != state.keys.dataset) {
          draft.version = initialState.version;
        }

        if (action.clear || !draft.data) {
          draft.loading = true;
          draft.version = initialState.version;
          draft.data = false;
        } else {
          draft.updating = true;
        }

        break;

      case UPDATE:
        draft.updating = true;
        break;

      case FAILED:
        draft.loading = false;
        draft.updating = false;
        draft.error = _.get(action, 'error.response.message') || 'Sorry, some error has occurred.';
        window.scrollTo(0, 0);
        break;

      case FETCHED:
        draft.loading = false;
        draft.updating = false;
        draft.data = action.response;
        break;

      case TEST_CONNECTION:
        draft.testing = true;
        draft.testResult = false;
        break;

      case TEST_CONNECTION_SUCCESS:
        draft.testing = false;
        draft.testResult = _.get(action, 'response.data');
        break;
    }
  });

export default dataSourceReducer;
