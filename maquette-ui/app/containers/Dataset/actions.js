/*
 *
 * Dataset actions
 *
 */

import { 
  INIT,
  FAILED,
  FETCHED,

  CREATE_DATA_ACCESS_REQUEST, 
  CREATE_DATA_ACCESS_REQUEST_FAILED, 
  CREATE_DATA_ACCESS_REQUEST_SUCCESS,

  GRANT_ACCESS,
  GRANT_ACCESS_FAILED,
  GRANT_ACCESS_SUCCESS,

  REVOKE_ACCESS, 
  REVOKE_ACCESS_FAILED,
  REVOKE_ACCESS_SUCCESS, 

  SELECT_VERSION,

  UPDATE_DATA_ACCESS_REQUEST,
  UPDATE_DATA_ACCESS_REQUEST_FAILED,
  UPDATE_DATA_ACCESS_REQUEST_SUCCESS,

  UPDATE_DATASET, 
  UPDATE_DATASET_FAILED, 
  UPDATE_DATASET_SUCCESS } from './constants';

export function init(project, dataset) {
  return {
    type: INIT,
    project,
    dataset
  };
}

export function failed(key, error) {
  return {
    type: FAILED,
    key,
    error
  };
}

export function fetched(key, response) {
  return {
    type: FETCHED,
    key,
    response
  };
}

export function createDataAccessRequest(project, dataset, origin, reason) {
  return {
    type: CREATE_DATA_ACCESS_REQUEST,
    project,
    dataset,
    origin,
    reason
  };
}

export function createDataAccessRequestFailed(error) {
  return {
    type: CREATE_DATA_ACCESS_REQUEST_FAILED,
    error
  };
}

export function createDataAccessRequestSuccess(response) {
  return {
    type: CREATE_DATA_ACCESS_REQUEST_SUCCESS,
    response
  };
}

export function getDataAccessRequestsSuccess(response) {
  return {
    type: GET_DATA_ACCESS_REQUESTS_SUCCESS,
    response
  };
}

export function getDatasetSuccess(response) {
  return {
    type: GET_DATASET_SUCCESS,
    response
  };
}

export function getProjectSuccess(name, response) {
  return {
    type: GET_PROJECT_SUCCESS,
    name,
    response
  };
}

export function getVersionsSuccess(response) {
  return {
    type: GET_VERSIONS_SUCCESS,
    response
  };
}

export function grantAccess(project, dataset, name) {
  return {
    type: GRANT_ACCESS,
    project,
    dataset,
    name
  }
 }
 
 export function grantAccessFailed(error) {
   return {
     type: GRANT_ACCESS_FAILED,
     error
   }
 }
 
 export function grantAccessSuccess(response) {
   return {
     type: GRANT_ACCESS_SUCCESS,
     response
   }
 }
 
 export function revokeAccess(project, dataset, name) {
   return {
     type: REVOKE_ACCESS,
     project,
     dataset,
     name
   }
  }
  
  export function revokeAccessFailed(error) {
    return {
      type: REVOKE_ACCESS_FAILED,
      error
    }
  }
  
  export function revokeAccessSuccess(response) {
    return {
      type: REVOKE_ACCESS_SUCCESS,
      response
    }
  }

export function selectVersion(version) {
  return {
    type: SELECT_VERSION,
    version
  };
}

export function updateDataAccessRequest(command, args) {
  return {
    type: UPDATE_DATA_ACCESS_REQUEST,
    command,
    args
  };
}

export function updateDataAccessRequestFailed(error) {
  return {
    type: UPDATE_DATA_ACCESS_REQUEST_FAILED,
    error
  }
}

export function updateDataAccessRequestSuccess(response) {
  return {
    type: UPDATE_DATA_ACCESS_REQUEST_SUCCESS,
    response
  };
}

export function updateDataset(project, dataset, name, title, summary, visibility, classification, personalInformation) {
  return {
    type: UPDATE_DATASET,
    project,
    dataset,
    name,
    title,
    summary,
    visibility,
    classification,
    personalInformation
  };
}

export function updateDatasetFailed(error) {
  return {
    type: UPDATE_DATASET_FAILED,
    error
  };
}

export function updateDatasetSuccess(response) {
  return {
    type: UPDATE_DATASET_SUCCESS,
    response
  };
}