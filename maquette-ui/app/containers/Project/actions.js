/*
 *
 * Project actions
 *
 */

import { 
  GET_DATASETS_FAILED, GET_DATASETS_SUCCESS, 
  GET_PROJECT, GET_PROJECT_SUCCESS, GET_PROJECT_FAILED } from './constants';

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