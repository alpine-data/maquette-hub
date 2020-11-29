/*
 *
 * Dataset actions
 *
 */

import { 
  LOAD,
  UPDATE,
  FAILED,
  FETCHED,
  SELECT_VERSION,
  DISMISS_ERROR } from './constants';

export function load(dataset, clear = false) {
  return {
    type: LOAD,
    dataset,
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

export function selectVersion(version) {
  return {
    type: SELECT_VERSION,
    version
  };
};

export function dismissError() {
  return {
    type: DISMISS_ERROR
  };
};
