/*
 *
 * CreateDataset actions
 *
 */

import { 
  CREATE_DATASET, CREATE_DATASET_FAILURE, CREATE_DATASET_SUCCESS, 
  LOAD_PROJECTS, LOAD_PROJECTS_FAILURE, LOAD_PROJECTS_SUCCESS } from './constants';

export function createDataset(project, title, name, summary, visibility, classification, personalInformation) {
  return {
    type: CREATE_DATASET,
    project, title, name, summary, visibility, classification, personalInformation
  };
}

export function createDatasetFailure(project, name, error) {
  return {
    type: CREATE_DATASET_FAILURE,
    project,
    name,
    error
  }
}

export function createDatasetSuccess(project, name, response) {
  return {
    type: CREATE_DATASET_SUCCESS,
    project,
    name, 
    response
  };
}

export function loadProjects(user) {
  return { 
    type: LOAD_PROJECTS,
    user
  }
}

export function loadProjectsFailure(error) {
  return {
    type: LOAD_PROJECTS_FAILURE,
    error
  };
}

export function loadProjectsSuccess(user, response) {
  return {
    type: LOAD_PROJECTS_SUCCESS,
    user,
    response
  }
}
