/*
 *
 * Dataset actions
 *
 */

import { 
  CREATE_DATA_ACCESS_REQUEST, CREATE_DATA_ACCESS_REQUEST_FAILED, CREATE_DATA_ACCESS_REQUEST_SUCCESS,
  GET_DATA_ACCESS_REQUESTS, GET_DATA_ACCESS_REQUESTS_FAILED, GET_DATA_ACCESS_REQUESTS_SUCCESS,
  GET_PROJECTS_SUCCESS, GET_PROJECTS_FAILED,
  GET_PROJECT_FAILED, GET_PROJECT_SUCCESS,
  GET_DATASET, GET_DATASET_FAILED, GET_DATASET_SUCCESS,
  UPDATE_DATA_ACCESS_REQUEST, UPDATE_DATA_ACCESS_REQUEST_FAILED, UPDATE_DATA_ACCESS_REQUEST_SUCCESS } from './constants';

export function createDataAccessRequest(project, dataset, origin, reason) {
  return {
    type: CREATE_DATA_ACCESS_REQUEST,
    project,
    dataset,
    origin,
    reason
  }
}

export function createDataAccessRequestFailed(error) {
  return {
    type: CREATE_DATA_ACCESS_REQUEST_FAILED,
    error
  }
}

export function createDataAccessRequestSuccess(response) {
  return {
    type: CREATE_DATA_ACCESS_REQUEST_SUCCESS,
    response
  };
}

export function getDataAccessRequests(project, dataset) {
  return {
    type: GET_DATA_ACCESS_REQUESTS,
    project,
    dataset
  }
}

export function getDataAccessRequestsFailed(error) {
  return {
    type: GET_DATA_ACCESS_REQUESTS_FAILED,
    error
  }
}

export function getDataAccessRequestsSuccess(response) {
  return {
    type: GET_DATA_ACCESS_REQUESTS_SUCCESS,
    response
  }
}

export function getDataset(project, dataset) {
  return {
    type: GET_DATASET,
    project,
    dataset
  };
}

export function getDatasetFailed(error) {
  return {
    type: GET_DATASET_FAILED,
    error
  };
}

export function getDatasetSuccess(project, dataset, response) {
  return {
    type: GET_DATASET_SUCCESS,
    project,
    dataset,
    response
  }
}

export function getProjectFailed(error) {
  return {
    type: GET_PROJECT_FAILED,
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

export function getProjectsFailed(error) {
  return {
    type: GET_PROJECTS_FAILED,
    error
  };
}

export function getProjectsSuccess(response) {
  return {
    type: GET_PROJECTS_SUCCESS,
    response
  };
}

export function updateDataAccessRequest(command, args) {
  return {
    type: UPDATE_DATA_ACCESS_REQUEST,
    command,
    args
  }
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
  }
}