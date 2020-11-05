/*
 *
 * CreateProject actions
 *
 */

import { CREATE_PROJECT, CREATE_PROJECT_FAILED, CREATE_PROJECT_SUCCESS } from './constants';

export function createProject({ title, name, owner, description }) {
  return {
    type: CREATE_PROJECT,
    title,
    name,
    owner,
    description
  };
}

export function CreateProjectFailed(name, error) {
  return {
    type: CREATE_PROJECT_FAILED,
    name,
    error
  }
}

export function createProjectSuccess(name, response) {
  return {
    type: CREATE_PROJECT_SUCCESS,
    name,
    response
  };
}
