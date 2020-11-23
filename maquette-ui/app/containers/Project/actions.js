/*
 *
 * Project actions
 *
 */

import {
  INIT, 
  FAILED,
  GET_DATA_ASSETS_SUCCESS,
  GET_PROJECT_SUCCESS,
  GET_SANDBOXES_SUCCESS,
  GET_STACKS_SUCCESS,

  GRANT_ACCESS, GRANT_ACCESS_SUCCESS, GRANT_ACCESS_FAILED,
  REVOKE_ACCESS, REVOKE_ACCESS_FAILED, REVOKE_ACCESS_SUCCESS,
  UPDATE_PROJECT, UPDATE_PROJECT_FAILED, UPDATE_PROJECT_SUCCESS
} from './constants';

export function init(project) {
  return {
    type: INIT,
    project
  };
}

export function failed(key, error) {
  return {
    type: FAILED,
    key,
    error
  };
}

export function getDataAssetsSuccess(response) {
  return {
    type: GET_DATA_ASSETS_SUCCESS,
    response
  }
}

export function getProjectSuccess(response) {
  return {
    type: GET_PROJECT_SUCCESS,
    response
  };
}

export function getSandboxesSuccess(response) {
  return {
    type: GET_SANDBOXES_SUCCESS,
    response
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