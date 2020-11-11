/*
 *
 * Dashboard actions
 *
 */

import { GET_PROJECTS, GET_PROJECTS_FAILED, GET_PROJECTS_SUCCESS} from './constants';

export function getProjects(user) {
  return {
    type: GET_PROJECTS,
    user
  };
}

export function getProjectsFailed(error) {
  return {
    type: GET_PROJECTS_FAILED,
    error
  }
}

export function getProjectsSuccess(response) {
  return {
    type: GET_PROJECTS_SUCCESS,
    response
  };
}
