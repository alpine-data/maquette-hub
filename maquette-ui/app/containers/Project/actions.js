/*
 *
 * Project actions
 *
 */

import { 
  LOAD,
  UPDATE,
  FAILED,
  FETCHED,
  DISMISS_ERROR } from './constants';

export function load(project, clear = false) {
  return {
    type: LOAD,
    project,
    clear
  };
};

export function update(command, request) {
  return {
    type: UPDATE,
    command,
    request
  }
};

export function failed(error) {
  return {
    type: FAILED,
    error
  };
};

export function fetched(response) {
  return {
    type: FETCHED,
    response
  };
};

export function dismissError() {
  return {
    type: DISMISS_ERROR
  };
};
