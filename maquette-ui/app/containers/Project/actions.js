/*
 *
 * Project actions
 *
 */

import { 
  GET_DATASETS_FAILED, GET_DATASETS_SUCCESS, 
  GET_PROJECT, GET_PROJECT_SUCCESS, GET_PROJECT_FAILED,
  GRANT_ACCESS, GRANT_ACCESS_FAILED, GRANT_ACCESS_SUCCESS,
  REVOKE_ACCESS, REVOKE_ACCESS_FAILED, REVOKE_ACCESS_SUCCESS,
  UPDATE_PROJECT, UPDATE_PROJECT_FAILED, UPDATE_PROJECT_SUCCESS, GET_SANDBOXES, GET_SANDBOXES_FAILED, GET_SANDBOXES_SUCCESS, GET_STACKS, GET_STACKS_SUCCESS, GET_STACKS_FAILED } from './constants';

export function getDatasetsFailed(name, error) {
  return {
    type: GET_DATASETS_FAILED,
    name,
    error
  }
}

export function getDatasetsSuccess(name, response) {
  return {
    type: GET_DATASETS_SUCCESS,
    name,
    response
  }
}

export function getProject(name, clear = true) {
  return {
    type: GET_PROJECT,
    name,
    clear
  };
}

export function getProjectFailed(name, error) {
  return {
    type: GET_PROJECT_FAILED,
    name,
    error
  };
}

export function getProjectSuccess(name, response) {
  return {
    type: GET_PROJECT_SUCCESS,
    name,
    response
  };
}

export function getSandboxes(project) {
  return {
    type: GET_SANDBOXES,
    project
  };
}

export function getSandboxesFailed(error) {
  return {
    type: GET_SANDBOXES_FAILED,
    error
  }
}

export function getSandboxesSuccess(response) {
  return {
    type: GET_SANDBOXES_SUCCESS,
    response
  }
}

export function getStacks() {
  return {
    type: GET_STACKS
  };
}

export function getStacksFailed(error) {
  return {
    type: GET_STACKS_FAILED,
    error
  }
}

export function getStacksSuccess(response) {
  return {
    type: GET_STACKS_SUCCESS,
    response
  };
}

export function grantAccess(project, type, name) {
 return {
   type: GRANT_ACCESS,
   project,
   authorizationType: type, 
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

export function revokeAccess(project, type, name) {
  return {
    type: REVOKE_ACCESS,
    project,
    authorizationType: type, 
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

 export function updateProject(project, name, title, summary) {
   return {
     type: UPDATE_PROJECT,
     project,
     name,
     title,
     summary
   };
 }

 export function updateProjectSuccess(response) {
   return {
     type: UPDATE_PROJECT_SUCCESS,
     response
   };
 }

 export function updateProjectFailed(error) {
   return {
     type: UPDATE_PROJECT_FAILED,
     error
   }
 }