/*
 *
 * Dataset reducer
 *
 */
import produce from 'immer';
import { 
  CREATE_DATA_ACCESS_REQUEST, CREATE_DATA_ACCESS_REQUEST_FAILED, CREATE_DATA_ACCESS_REQUEST_SUCCESS,
  GET_DATA_ACCESS_REQUESTS, GET_DATA_ACCESS_REQUESTS_FAILED, GET_DATA_ACCESS_REQUESTS_SUCCESS,
  GET_DATASET, GET_DATASET_SUCCESS, GET_DATASET_FAILED,
  GET_PROJECT_FAILED, GET_PROJECT_SUCCESS, GET_PROJECTS_FAILED, GET_PROJECTS_SUCCESS,
  GET_VERSIONS_FAILED, GET_VERSIONS_SUCCESS,
  SELECT_VERSION,
  UPDATE_DATA_ACCESS_REQUEST, UPDATE_DATA_ACCESS_REQUEST_FAILED, UPDATE_DATA_ACCESS_REQUEST_SUCCESS } from './constants';

export const initialState = {
  id: false,
  error: false,

  create_data_access_request: {
    loading: false,
    error: false,
    response: false
  },

  data_access_requests: {
    loading: false,
    updating: false,
    error: false,
    requests: false
  },
  
  dataset_loading: false,
  project_loading: false,
  projects_loading: false,

  loading: false,
  dataset: false,
  project: false,
  projects: false,
  versions: false,

  version: false
};

const datasetReducer = (state = initialState, action) =>
  produce(state, (draft) => {
    switch (action.type) {
      case CREATE_DATA_ACCESS_REQUEST:
        draft.create_data_access_request.loading = true;
        draft.create_data_access_request.error = false;
        break;

      case CREATE_DATA_ACCESS_REQUEST_FAILED:
        draft.create_data_access_request.loading = false;
        draft.create_data_access_request.error = _.get(action, 'error.response.message') || 'Unknown error occurred creating the request';
        break;

      case CREATE_DATA_ACCESS_REQUEST_SUCCESS:
        draft.create_data_access_request.loading = false;
        draft.create_data_access_request.response = action.response;
        break;

      case GET_DATASET:
        draft.dataset = false;
        draft.project = false;
        draft.projects = false;
        draft.versions = false;
        draft.version = false;
        draft.data_access_requests = initialState.create_data_access_request;

        draft.dataset_loading = true;
        draft.data_access_requests.loading = true;
        draft.project_loading = true;
        draft.projects_loading = true;
        draft.id = `${action.project}/${action.dataset}`
        break;

      case GET_DATASET_FAILED:
        draft.dataset_loading = false;
        draft.error = _.get(action, 'error.response.message') || 'Unknown error occurred loading the dataset';
        break;

      case GET_DATASET_SUCCESS:
        draft.dataset_loading = false;
        draft.dataset = action.response.data;
        break;

      case GET_DATA_ACCESS_REQUESTS:
        draft.data_access_requests.loading = true;
        draft.data_access_requests.error = false;

      case GET_DATA_ACCESS_REQUESTS_FAILED:
        draft.data_access_requests.loading = false;
        draft.data_access_requests.error = _.get(action, 'error.response.message') || 'Unknown error occurred loading the dataset\'s access requests';
        break;

      case GET_DATA_ACCESS_REQUESTS_SUCCESS:
        draft.data_access_requests.loading = false;
        draft.data_access_requests.requests = action.response;
        break;

      case GET_PROJECT_FAILED:
        draft.project_loading = false;
        draft.error = _.get(action, 'error.response.message') || 'Unkown error occurred loading project information';
        break;

      case GET_PROJECT_SUCCESS:
        draft.project_loading = false;
        draft.project = action.response.data;
        break;

      case GET_PROJECTS_FAILED:
        draft.projects_loading = false;
        draft.error = _.get(action, 'error.response.message') || 'Unkown error occurred loading projects';
        break;

      case GET_PROJECTS_SUCCESS:
        draft.projects_loading = false;
        draft.projects = action.response;
        break;

      case GET_VERSIONS_FAILED:
        draft.error = _.get(action, 'error.response.message') || 'Unkown error occurred loading projects';
        break;

      case GET_VERSIONS_SUCCESS:
        draft.versions = action.response;
        draft.version = _.size(action.response) > 0 && action.response[0].version;
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

    draft.loading = draft.data_access_requests_loading || draft.projects_loading ||  draft.project_loading || draft.dataset_loading;
  });

export default datasetReducer;
