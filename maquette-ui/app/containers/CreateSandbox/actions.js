/*
 *
 * CreateSandbox actions
 *
 */

import { 
  CREATE_SANDBOX, CREATE_SANDBOX_SUCCESS, 
  INITIALIZE, FAILED, 
  GET_PROJECTS_SUCCESS, GET_STACKS_SUCCESS } from './constants';

export function createSandbox(request) {
  return {
    type: CREATE_SANDBOX,
    ...request
  };
}

export function createSandboxSuccess(response) {
  return {
    type: CREATE_SANDBOX_SUCCESS,
    response
  };
}

export function initialize() {
  return {
    type: INITIALIZE
  };
}

export function failed(key, error) {
  return {
    type: FAILED,
    key,
    error
  }
}

export function getProjectsSuccess(response) {
  return {
    type: GET_PROJECTS_SUCCESS,
    response
  }
}

export function getStacksSuccess(response) {
  return {
    type: GET_STACKS_SUCCESS,
    response
  };
}
