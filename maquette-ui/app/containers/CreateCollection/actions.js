/*
 *
 * CreateCollection actions
 *
 */

import { 
  FETCH, FETCHED, ERROR, CREATE, CREATE_FAILURE, CREATE_SUCCESS } from './constants';

export function fetch() {
  return {
    type: FETCH
  };
};

export function create(data) {
  return {
    type: CREATE,
    ...data
  };
};

export function createFailure(error) {
  return {
    type: CREATE_FAILURE,
    error
  };
};

export function createSuccess(name, response) {
  return {
    type: CREATE_SUCCESS,
    name, 
    response
  };
};
