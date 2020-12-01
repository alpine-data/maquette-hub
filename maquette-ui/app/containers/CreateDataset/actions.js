/*
 *
 * CreateDataset actions
 *
 */

import { 
  CREATE, CREATE_FAILURE, CREATE_SUCCESS } from './constants';

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
