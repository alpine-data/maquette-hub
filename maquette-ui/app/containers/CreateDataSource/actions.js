/*
 *
 * CreateDataSource actions
 *
 */

import { 
  CREATE, CREATE_SUCCESS, CREATE_FAILED,
  TEST_CONNECTION, TEST_CONNECTION_SUCCESS } from './constants';

export function create(data) {
  return {
    type: CREATE,
    ...data
  }
}

export function createFailed(error) {
  return {
    type: CREATE_FAILED,
    error
  }
}

export function createSuccess(response) {
  return {
    type: CREATE_SUCCESS,
    response
  }
}

export function testConnection(data) {
  return {
    type: TEST_CONNECTION,
    ...data
  };
}

export function testConnectionSuccess(response) {
  return {
    type: TEST_CONNECTION_SUCCESS,
    response
  }
}
