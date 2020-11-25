/*
 *
 * Dataset reducer
 *
 */
import produce from 'immer';
import { 
  INIT,
  FAILED,
  FETCHED,

  CREATE_DATA_ACCESS_REQUEST, 
  CREATE_DATA_ACCESS_REQUEST_FAILED, 
  CREATE_DATA_ACCESS_REQUEST_SUCCESS,

  SELECT_VERSION,

  UPDATE_DATA_ACCESS_REQUEST, 
  UPDATE_DATA_ACCESS_REQUEST_FAILED, 
  UPDATE_DATA_ACCESS_REQUEST_SUCCESS } from './constants';

export const initialState = {
  dataset: false,
  project: false,
  requests: false,
  versions: false,
  version: '1.0.0',

  errors: {},
  loading: []
};

const datasetReducer = (state = initialState, action) =>
  produce(state, (draft) => {
    switch (action.type) {
      case INIT:
        draft.errors = initialState.errors;
        draft.loading = _.concat(draft.loading, ['dataset', 'project', 'requests', 'versions'])
        break;

      case FAILED:
        draft.loading = _.without(draft.loading, action.key);
        draft.errors = _.assign(draft.errors, { [action.key]: action.error });
        break;

      case FETCHED:
        draft.loading = _.without(draft.loading, action.key);
        draft[action.key] = _.get(action, 'response.data') || _.get(action, 'response') || {};

        if (action.key == 'versions') {
          draft.version = _.first(draft['versions']).version || '1.0.0';
        }

        break;

      case SELECT_VERSION:
        draft.version = action.version;
        break;

      case UPDATE_DATA_ACCESS_REQUEST:
        draft.data_access_requests.error = false
        draft.data_access_requests.updating = true;
        break;

      case UPDATE_DATA_ACCESS_REQUEST_FAILED:
        draft.data_access_requests.updating = false;
        draft.data_access_requests.error = _.get(action, 'error.response.message') || 'Unkown error occurred updating data access request';
        break;

      case UPDATE_DATA_ACCESS_REQUEST_SUCCESS:
        draft.data_access_requests.updating = false;
        break;      
    }
  });

export default datasetReducer;
