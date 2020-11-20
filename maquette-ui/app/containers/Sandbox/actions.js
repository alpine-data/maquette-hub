/*
 *
 * Sandbox actions
 *
 */

import { 
  INITIALIZE, FAILED,
  GET_SANDBOX_SUCCESS, GET_STACKS_SUCCESS, GET_PROJECT_SUCCESS } from './constants';

export function initialize(project, sandbox) {
  return {
    type: INITIALIZE,
    project,
    sandbox
  };
}

export function failed(key, error) {
  return {
    type: FAILED,
    key,
    error
  };
}

export function getSandboxSuccess(response) {
  return {
    type: GET_SANDBOX_SUCCESS,
    response
  };
}

export function getStacksSuccess(response) {
  return {
    type: GET_STACKS_SUCCESS,
    response
  };
}

export function getProjectSuccess(response) {
  return {
    type: GET_PROJECT_SUCCESS,
    response
  }
}
