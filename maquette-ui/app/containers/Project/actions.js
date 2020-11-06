/*
 *
 * Project actions
 *
 */

import { GET_PROJECT, GET_PROJECT_SUCCESS, GET_PROJECT_FAILED } from './constants';

export function getProject(name) {
  return {
    type: GET_PROJECT,
    name
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